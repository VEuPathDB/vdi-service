package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.BadRequestException
import org.slf4j.LoggerFactory
import org.veupathdb.lib.container.jaxrs.errors.FailedDependencyException
import org.veupathdb.lib.jaxrs.raml.multipart.JaxRSMultipartUpload
import org.veupathdb.service.vdi.config.Options
import org.veupathdb.service.vdi.generated.model.DatasetPostRequest
import org.veupathdb.service.vdi.generated.model.toDatasetMeta
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.service.vdi.service.users.getCurrentQuotaUsage
import org.veupathdb.service.vdi.util.*
import org.veupathdb.service.vdi.util.URLFetchException
import org.veupathdb.service.vdi.util.fetchContent
import org.veupathdb.service.vdi.util.toJavaURL
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.compression.Tar
import org.veupathdb.vdi.lib.common.compression.Zip
import org.veupathdb.vdi.lib.common.compression.Zip.getZipType
import org.veupathdb.vdi.lib.common.compression.ZipType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.fs.TempFiles
import org.veupathdb.vdi.lib.common.model.*
import vdi.component.db.app.AppDatabaseRegistry
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.DatasetImpl
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.db.cache.model.DatasetMetaImpl
import vdi.component.db.cache.withTransaction
import vdi.component.metrics.Metrics
import vdi.component.plugin.mapping.PluginHandlers
import java.net.MalformedURLException
import java.net.URL
import java.nio.file.Path
import java.time.OffsetDateTime
import java.util.concurrent.Executors
import java.util.zip.ZipException
import kotlin.io.path.*
import kotlin.math.max

private val log = LoggerFactory.getLogger("create-dataset.kt")

private val WorkPool = Executors.newFixedThreadPool(10)

@OptIn(ExperimentalPathApi::class)
fun createDataset(
  userID: UserID,
  datasetID: DatasetID,
  entity: DatasetPostRequest,
) {
  log.trace("createDataset(userID={}, datasetID={}, entity={})", userID, datasetID, entity)

  val datasetMeta = entity.toDatasetMeta(userID)

  val handler = PluginHandlers[datasetMeta.type.name, datasetMeta.type.version]
    ?: throw BadRequestException("target dataset type is unknown to the VDI service")

  for (projectID in datasetMeta.projects) {
    if (!handler.appliesToProject(projectID))
      throw BadRequestException("target dataset type does not apply to project $projectID")

    if (!AppDatabaseRegistry.contains(projectID))
      throw BadRequestException("unrecognized target project")
  }

  val (tempDirectory, uploadFile) = CacheDB().withTransaction {
    it.tryInsertDataset(DatasetImpl(
      datasetID    = datasetID,
      typeName     = datasetMeta.type.name,
      typeVersion  = datasetMeta.type.version,
      ownerID      = userID,
      isDeleted    = false,
      created      = datasetMeta.created,
      importStatus = DatasetImportStatus.Queued,
      origin       = datasetMeta.origin,
      inserted     = OffsetDateTime.now(),
    ))
    it.tryInsertDatasetMeta(DatasetMetaImpl(
      datasetID   = datasetID,
      visibility  = datasetMeta.visibility,
      name        = datasetMeta.name,
      summary     = datasetMeta.summary,
      description = datasetMeta.description,
      sourceURL   = datasetMeta.sourceURL,
    ))
    it.tryInsertImportControl(datasetID, DatasetImportStatus.Queued)
    it.tryInsertDatasetProjects(datasetID, datasetMeta.projects)
    it.tryInsertSyncControl(VDISyncControlRecord(
      datasetID     = datasetID,
      sharesUpdated = OriginTimestamp,
      dataUpdated   = OriginTimestamp,
      metaUpdated   = OriginTimestamp
    ))

    // TODO: Post release!
    //       The following call represents a 'unified' path for handling uploads
    //       whether they are direct or via URL.  This leaves us open to proxy
    //       timeouts in the case of URL uploads if the file transfer between the
    //       VDI service and the remote server takes too long.
    //
    //       To handle this, the 'unified' path will need to be split into two
    //       paths:
    //       - Direct upload path
    //       - URL upload path
    //
    //       For the direct upload path, the upload file will need to be copied to
    //       a new temp directory (as the rest service thread will delete the
    //       original upload file).  Then the new thread can be forked and the
    //       file uploaded to MinIO.
    //
    //       For the URL upload path, the URL will need to be validated before
    //       starting the new thread.  Then the new thread will be forked and the
    //       target file downloaded into a temp directory in that thread before
    //       being uploaded to MinIO (also in that forked thread).
    try { entity.fetchDatasetFile() }
    catch (e: Throwable) {
      CacheDB().withTransaction { it.updateImportControl(datasetID, DatasetImportStatus.Failed) }
      throw e
    }
  }

  Metrics.Upload.queueSize.inc()
  WorkPool.submit {
    try {
      uploadFiles(userID, datasetID, tempDirectory, uploadFile, datasetMeta)
    } finally {
      Metrics.Upload.queueSize.dec()
      tempDirectory.deleteRecursively()
    }
  }
}

@OptIn(ExperimentalPathApi::class)
private fun uploadFiles(
  userID: UserID,
  datasetID: DatasetID,
  tempDirectory: Path,
  uploadFile: Path,
  datasetMeta: VDIDatasetMeta,
) {
  val cacheDB = CacheDB()

  // Get a handle on the temp file that will be uploaded to the S3 store (MinIO)
  TempFiles.withTempDirectory { directory ->
    TempFiles.withTempPath { archive ->
      try {
        log.debug("Verifying file sizes for dataset {}/{} to ensure the user quota is not exceeded.", userID, datasetID)
        verifyFileSize(uploadFile, userID)

        log.debug("Repacking input file for dataset {}/{}.", userID, datasetID)
        val sizes = uploadFile.repack(into = archive, using = directory)

        log.debug("uploading raw user data to S3 for new dataset {}/{}", userID, datasetID)
        DatasetStore.putImportReadyZip(userID, datasetID, archive::inputStream)

        cacheDB.withTransaction { it.tryInsertUploadFiles(datasetID, sizes) }
      } catch (e: Throwable) {
        log.error("user dataset upload to minio failed: ", e)
        Metrics.Upload.failed.inc();
        cacheDB.withTransaction { it.updateImportControl(datasetID, DatasetImportStatus.Failed) }
        throw e
      } finally {
        uploadFile.deleteIfExists()
        tempDirectory.deleteRecursively()
      }
    }
  }

  try {
    log.debug("uploading dataset metadata to S3 for new dataset {}/{}", userID, datasetID)
    DatasetStore.putDatasetMeta(userID, datasetID, datasetMeta)
  } catch (e: Throwable) {
    log.error("user dataset meta file upload to minio failed: ", e)
    cacheDB.withTransaction { it.updateImportControl(datasetID, DatasetImportStatus.Failed) }
    throw e
  }
}

private fun verifyFileSize(file: Path, userID: UserID) {
  val fileSize = file.fileSize()

  if (fileSize > Options.Quota.maxUploadSize.toLong())
    throw BadRequestException("upload file size larger than the max permitted file size of "
      + Options.Quota.maxUploadSize.toString() + " bytes")

  val diff = max(0L, Options.Quota.quotaLimit.toLong() - getCurrentQuotaUsage(userID))

  if (fileSize > diff)
    throw BadRequestException("upload file size is larger than the remaining space allowed by the user quota " +
      "($diff bytes)")
}

/**
 * Repacks the receiver file or archive into a zip file for upload to S3.
 *
 * @receiver Path to the file or archive to repack.
 *
 * @param into Path to the new archive that should be created.
 *
 * @param using Temp directory to use when unpacking/repacking the upload files.
 *
 * @return A map of upload files and their sizes.
 */
private fun Path.repack(into: Path, using: Path): List<VDIDatasetFileInfo> {
  // If it resembles a zip file
  return if (name.endsWith(".zip")) {
    repackZip(into, using)
  }

  // If it resembles a tar file
  else if (name.endsWith(".tar.gz") || name.endsWith(".tgz")) {
    repackTar(into, using)
  }

  else {
    repackRaw(into)
  }
}

/**
 * Repacks the receiver zip file into a new highly compressed zip file for
 * upload to S3.
 *
 * @receiver Path to the zip file to repack.
 *
 * @param into Path to the new archive that should be created.
 *
 * @param using Temp directory to use when unpacking/repacking the upload files.
 *
 * @return A map of upload files and their sizes.
 */
private fun Path.repackZip(into: Path, using: Path): List<VDIDatasetFileInfo> {
  log.trace("repacking zip file {} into {}", this, into)

  // Map of file names to sizes that will be stored in the postgres database.
  val files = ArrayList<VDIDatasetFileInfo>(12)

  // Validate that the zip appears usable.
  validateZip()

  // List of paths for the unpacked files
  val unpacked = ArrayList<Path>(12)

  // Iterate through the zip entries
  Zip.zipEntries(this)
    .forEach { (entry, input) ->

      // If the zip entry contains a slash, we reject it (we don't presently
      // allow subdirectories)
      if (entry.name.contains('/') || entry.isDirectory) {
        // If the archive contains that awful __MACOSX directory, skip it
        // silently.
        if (entry.name.startsWith("__MACOSX")) {
          return@forEach
        }

        throw BadRequestException("uploaded zip file must not contain subdirectories")
      }

      val tmpFile = using.resolve(entry.name)

      tmpFile.createFile()
      tmpFile.outputStream().use { out -> input.transferTo(out) }

      files.add(VDIDatasetFileInfo(entry.name, tmpFile.fileSize().toULong()))

      unpacked.add(tmpFile)
    }

  // ensure that the zip actually contained some files
  if (unpacked.isEmpty())
    throw BadRequestException("uploaded file was empty or was not a valid zip")

  log.info("Compressing file from {} into {}", unpacked, into)
  // recompress the files as a tgz file
  Zip.compress(into, unpacked)

  return files
}

/**
 * Repacks the receiver tar file into a new highly compressed zip file for
 * upload to S3.
 *
 * @receiver Path to the tar file to repack.
 *
 * @param into Path to the new archive that should be created.
 *
 * @param using Temp directory to use when unpacking/repacking the upload files.
 *
 * @return A map of upload files and their sizes.
 */
private fun Path.repackTar(into: Path, using: Path): List<VDIDatasetFileInfo> {
  log.trace("repacking tar {} into {}", this, into)

  // Output map of files to sizes that will be written to the postgres DB.
  val sizes = ArrayList<VDIDatasetFileInfo>(12)

  try {
    Tar.decompressWithGZip(this, using)
  } catch (e: ZipException) {
    throw BadRequestException("input file had gzipped tar extension but could not be packed as a gzipped tar")
  }

  val files = using.listDirectoryEntries()

  if (files.isEmpty())
    throw BadRequestException("uploaded file contained no files or was not a valid tar archive")

  for (file in files)
    sizes.add(VDIDatasetFileInfo(file.name, file.fileSize().toULong()))

  Zip.compress(into, files)

  return sizes
}

private fun Path.repackRaw(into: Path): List<VDIDatasetFileInfo> {
  log.trace("repacking raw file {} into {}", this, into)
  Zip.compress(into, listOf(this))
  return listOf(VDIDatasetFileInfo(name, fileSize().toULong()))
}

private data class FileReference(val tempDirectory: Path, val tempFile: Path)

/**
 * Resolves the upload dataset file and places it in a new temp directory.
 *
 * If the file was uploaded directly, that upload file will be copied into a new
 * temp directory to avoid the rest-server deleting the upload on request
 * completion.
 *
 * If the request provided a URL to a target file, that file will be downloaded
 * into a new temp directory.
 *
 * @return A [Pair] containing the temp directory path first and the temp file
 * path second.
 */
private fun DatasetPostRequest.fetchDatasetFile(): FileReference =
  file?.let {
    TempFiles.makeTempPath(it.name)
      .also { (_, tmpFile) -> it.copyTo(tmpFile.toFile(), true) }
      .let { (tmpDir, tmpFile) -> FileReference(tmpDir, tmpFile) }
  } ?: downloadRemoteFile()

@OptIn(ExperimentalPathApi::class)
private fun DatasetPostRequest.downloadRemoteFile(): FileReference {
  val url = try { url.toJavaURL() }
  catch (e: MalformedURLException) { throw BadRequestException("invalid file source: ${e.message}") }

  log.info("attempting to download a remote file from {}", url)

  // If the user gave us a URL then we have to download the contents of that
  // URL to a local file to be uploaded.   This is done to catch errors with
  // the URL or transfer before we start uploading to the dataset store.
  val fileName = url.safeFilename()

  if (fileName.isBlank())
    throw BadRequestException("could not determine file name or type from the given URL")

  val response = try { url.fetchContent() }
  catch (e: URLFetchException) { throw FailedDependencyException(this.url, e.message, e) }

  // If the remote server "successfully" returned an error code or some other
  // non-2xx code.
  if (!response.isSuccess)
    throw FailedDependencyException(this.url, "unexpected status code ${response.status}")

  // If for some reason the server returned nothing.
  if (!response.hasBody)
    throw FailedDependencyException(this.url, "remote server returned code ${response.status} with no content")

  val (tmpDir, tmpFile) = TempFiles.makeTempPath(fileName)

  // Try to download the file from the source URL.
  try {
    BoundedInputStream(JaxRSMultipartUpload.maxFileUploadSize, response.body!!) {
      BadRequestException("given source file URL pointed to a file that exceeded the max allowed upload size of " +
        "${JaxRSMultipartUpload.maxFileUploadSize} bytes.")
    }
      .use { inp -> tmpFile.outputStream().use { out -> inp.transferTo(out) } }
  } catch (e: Throwable) {
    log.error("content transfer from ${url.shortForm}", e)
    tmpFile.deleteIfExists()
    tmpDir.deleteRecursively()
    throw FailedDependencyException(
      this.url,
      "error occurred while attempting to download source file from ${url.shortForm}",
    )
  }

  return FileReference(tmpDir, tmpFile)
}

private fun Path.validateZip() {
  when (getZipType()) {
    ZipType.Empty -> throw BadRequestException("uploaded zip file is empty")
    ZipType.Standard -> { /* OK */ }
    ZipType.Spanned -> throw BadRequestException("uploaded zip file is part of a spanned archive")
    ZipType.Invalid -> throw BadRequestException("uploaded zip file is invalid")
  }
}

private fun URL.safeFilename(): String {
  val filename = path.trim('/')
    .let { it.substring(it.lastIndexOf('/') + 1) }

  // If the filename portion is not empty and is at most 128 characters, return
  // it.
  if (filename.length in 1..128)
    return filename

  // Since the filename is too long, attempt to construct a hostname based file
  // so the user has some clue as to what this file is when viewing the dataset
  // details.
  //
  // According to RFC-1035, domain name parts (labels) can be at most 63
  // characters, so we should be safe to just append '_download' to the domain
  // name label.
  return host.let {
    val idx2 = when (val i = it.lastIndexOf('.')) {
      -1   -> it.length
      else -> i
    }

    val idx1 = it.lastIndexOf('.', idx2-1)

    it.substring(idx1+1, idx2)
  } + "_download"
}
