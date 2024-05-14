package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.InternalServerErrorException
import org.slf4j.LoggerFactory
import org.veupathdb.lib.jaxrs.raml.multipart.JaxRSMultipartUpload
import org.veupathdb.service.vdi.config.Options
import org.veupathdb.service.vdi.generated.model.DatasetPostRequest
import org.veupathdb.service.vdi.generated.model.toInternalVisibility
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.service.vdi.service.users.getCurrentQuotaUsage
import org.veupathdb.service.vdi.util.BoundedInputStream
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
import vdi.component.plugin.mapping.PluginHandlers
import vdi.component.metrics.Metrics
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

    if (projectID !in AppDatabaseRegistry)
      throw BadRequestException("unrecognized target project")
  }

  CacheDB().withTransaction {
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
  }

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
  val (tempDirectory, uploadFile) = entity.getDatasetFile()

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
    throw BadRequestException("upload file size larger than the max permitted file size of " + Options.Quota.maxUploadSize.toString() + " bytes")

  val diff = max(0L, Options.Quota.quotaLimit.toLong() - getCurrentQuotaUsage(userID))

  if (fileSize > diff)
    throw BadRequestException("upload file size is larger than the remaining space allowed by the user quota ($diff bytes)")
}

private fun DatasetPostRequest.toDatasetMeta(userID: UserID) =
  VDIDatasetMetaImpl(
    type         = VDIDatasetTypeImpl(
      name    = meta.datasetType.name.lowercase(),
      version = meta.datasetType.version,
    ),
    projects     = meta.projects.toSet(),
    owner        = userID,
    name         = meta.name,
    summary      = meta.summary,
    description  = meta.description,
    visibility   = meta.visibility?.toInternalVisibility() ?: VDIDatasetVisibility.Private,
    origin       = meta.origin,
    sourceURL    = url,
    created      = meta.createdOn ?: OffsetDateTime.now(),
    dependencies = (meta.dependencies ?: emptyList()).map {
      VDIDatasetDependencyImpl(
        identifier  = it.resourceIdentifier,
        version     = it.resourceVersion,
        displayName = it.resourceDisplayName
      )
    },
  )

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

      // If the zip entry contains a slash, we reject it (we don't presently allow subdirectories)
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

      files.add(VDIDatasetFileInfoImpl(entry.name, tmpFile.fileSize()))

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
    sizes.add(VDIDatasetFileInfoImpl(file.name, file.fileSize()))

  Zip.compress(into, files)

  return sizes
}

private fun Path.repackRaw(into: Path): List<VDIDatasetFileInfo> {
  log.trace("repacking raw file {} into {}", this, into)
  Zip.compress(into, listOf(this))
  return listOf(VDIDatasetFileInfoImpl(name, fileSize()))
}

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
@OptIn(ExperimentalPathApi::class)
private fun DatasetPostRequest.getDatasetFile(): Pair<Path, Path> =
  if (file != null) {
    // If the user uploaded a file, then use that
    val (tempDir, tempFile) = TempFiles.makeTempPath(file.name)

    file.copyTo(tempFile.toFile(), true)

    tempDir to tempFile
  } else {
    // Try to construct a URL instance (validating that the URL is sane)
    val url = try { URL(url) }
    catch (e: Throwable) { throw BadRequestException("invalid source file URL given") }

    // If the user gave us a URL then we have to download the contents of that
    // URL to a local file to be uploaded.   This is done to catch errors with
    // the URL or transfer before we start uploading to the dataset store.
    val fileName = url.safeFilename()

    if (fileName.isBlank())
      throw BadRequestException("could not determine file name or type from the given URL")

    val paths = TempFiles.makeTempPath(fileName)

    // Try to establish a connection to the URL target (validating that the
    // target is reachable)
    val connection = try { url.openConnection() }
    catch (e: Throwable) { throw BadRequestException("given source file URL was unreachable") }

    // Try to download the file from the source URL.
    try {
      BoundedInputStream(JaxRSMultipartUpload.maxFileUploadSize, connection.getInputStream()) {
        BadRequestException("given source file URL pointed to a file that exceeded the max allowed upload size of ${JaxRSMultipartUpload.maxFileUploadSize} bytes.")
      }
        .use { inp -> paths.second.outputStream().use { out -> inp.transferTo(out) } }
    } catch (e: Throwable) {
      log.error("failed to download file from target URL", e)
      paths.second.deleteIfExists()
      paths.first.deleteRecursively()
      throw InternalServerErrorException("error occurred while attempting to download source file from the given URL")
    }

    paths
  }

private fun Path.validateZip() {
  when (getZipType()) {
    ZipType.Empty    -> throw BadRequestException("uploaded zip file is empty")
    ZipType.Standard -> { /* OK */ }
    ZipType.Spanned  -> throw BadRequestException("uploaded zip file is part of a spanned archive")
    ZipType.Invalid  -> throw BadRequestException("uploaded zip file is invalid")
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
