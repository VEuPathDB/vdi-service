@file:JvmName("DatasetUploadCommon")

package vdi.service.rest.server.services.dataset

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.WebApplicationException
import org.slf4j.Logger
import org.veupathdb.lib.container.jaxrs.errors.FailedDependencyException
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.nio.file.Path
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.concurrent.Executors
import java.util.zip.ZipException
import kotlin.io.path.*
import kotlin.math.max
import kotlin.math.min
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.CacheDBTransaction
import vdi.core.db.cache.model.DatasetImpl
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.withTransaction
import vdi.core.db.model.SyncControlRecord
import vdi.core.metrics.Metrics
import vdi.logging.markedLogger
import vdi.model.OriginTimestamp
import vdi.model.data.*
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.services.users.getCurrentQuotaUsage
import vdi.service.rest.util.*
import vdi.util.fs.TempFiles
import vdi.util.zip.*

// region Cache DB Interactions

fun <T> CacheDB.initializeDataset(
  userID: UserID,
  datasetID: DatasetID,
  datasetMeta: DatasetMetadata,
  fn: (CacheDBTransaction) -> T,
): T =
  withTransaction {
    it.tryInsertDataset(DatasetImpl(
      datasetID = datasetID,
      type = datasetMeta.type,
      ownerID = userID,
      isDeleted = false,
      created = datasetMeta.created,
      importStatus = DatasetImportStatus.Queued,
      origin = datasetMeta.origin,
      inserted = OffsetDateTime.now(),
    ))
    it.tryInsertDatasetMeta(datasetID, datasetMeta)
    it.tryInsertImportControl(datasetID, DatasetImportStatus.Queued)
    it.tryInsertDatasetProjects(datasetID, datasetMeta.installTargets)
    it.tryInsertSyncControl(SyncControlRecord(
      datasetID = datasetID,
      sharesUpdated = OriginTimestamp,
      dataUpdated = OriginTimestamp,
      metaUpdated = OriginTimestamp
    ))

    fn(it)
  }

// endregion Cache DB Interactions

// region Dataset File Processing

@OptIn(ExperimentalPathApi::class)
fun <T: ControllerBase> T.resolveDatasetFiles(
  data: Iterable<File>?,
  url: String?,
  meta: Iterable<File>?,
  uploadConfig: UploadConfig,
): UploadFileReferences {
  val tmpDir = TempFiles.makeTempDirectory()

  return try {
    if (data != null)
      resolveUploadFiles(data, meta ?: emptyList(), tmpDir)
    else
      resolveURLFile(url!!, meta ?: emptyList(), uploadConfig, tmpDir)
  } catch (e: Throwable) {
    tmpDir.deleteRecursively()
    throw e
  }
}

fun resolveUploadFiles(
  data: Iterable<File>,
  meta: Iterable<File>,
  tmpDir: Path,
): UploadFileReferences =
  UploadFileReferences(
    tempDir = tmpDir,
    data = data.asSequence()
      .map { it.copyTo(tmpDir.resolve(it.name).toFile()) }
      .map { it.toPath() }
      .toList(),
    meta = meta.asSequence()
      .map { it.copyTo(tmpDir.resolve(it.name).toFile()) }
      .map { it.toPath() }
      .toList()
  )

fun <T: ControllerBase> T.resolveURLFile(
  url: String,
  meta: Iterable<File>,
  uploadConfig: UploadConfig,
  tmpDir: Path,
): UploadFileReferences =
  UploadFileReferences(
    tempDir = tmpDir,
    data = listOf(downloadRemoteFile(url.toURL(), uploadConfig, tmpDir)),
    meta = meta.asSequence()
      .map { it.copyTo(tmpDir.resolve(it.name).toFile()) }
      .map { it.toPath() }
      .toList()
  )

data class UploadFileReferences(val tempDir: Path, val data: List<Path>, val meta: List<Path>)

private val WorkPool = Executors.newFixedThreadPool(10)

@OptIn(ExperimentalPathApi::class)
fun <T: ControllerBase> T.submitUpload(
  datasetID: DatasetID,
  uploadRefs: UploadFileReferences,
  datasetMeta: DatasetMetadata,
  uploadConfig: UploadConfig,
) {
  Metrics.Upload.queueSize.inc()
  WorkPool.submit {
    try {
      uploadFiles(datasetID, uploadRefs, datasetMeta, uploadConfig)
    } finally {
      Metrics.Upload.queueSize.dec()
      uploadRefs.tempDir.deleteRecursively()
    }
  }
}

fun <T: ControllerBase> T.uploadFiles(
  datasetID: DatasetID,
  uploadRefs: UploadFileReferences,
  datasetMeta: DatasetMetadata,
  uploadConfig: UploadConfig,
) {
  val logger = markedLogger(userID, datasetID)

  verifyFileSize(uploadRefs, uploadConfig)

  try {
    TempFiles.withTempPath { archive ->
      val sizes = if (uploadRefs.data.size == 1) {
        logger.debug("input was a single file, attempting to (re)pack")
        TempFiles.withTempDirectory { dir -> uploadRefs.data[0].repack(into = archive, using = dir, logger = logger) }
      } else {
        uploadRefs.data.pack(into = archive)
      }

      logger.debug("uploading manifest")
      DatasetStore.putManifest(userID, datasetID, DatasetManifest(sizes, emptyList()))

      logger.debug("uploading raw dataset data")
      DatasetStore.putImportReadyZip(userID, datasetID, archive::inputStream)

      CacheDB().withTransaction { it.tryInsertUploadFiles(datasetID, sizes) }
    }
  } catch (e: Throwable) {
    if (e is WebApplicationException && (e.response?.status ?: 500) in 400..499) {
      logger.info("rejecting dataset upload for user error: {}", e.message)
      CacheDB().withTransaction {
        it.updateImportControl(datasetID, DatasetImportStatus.Invalid)
        e.message?.let { msg -> it.tryInsertImportMessages(datasetID, msg) }
      }
    } else {
      logger.error("user dataset upload to minio failed: ", e)
      Metrics.Upload.failed.inc()
      CacheDB().withTransaction { it.updateImportControl(datasetID, DatasetImportStatus.Failed) }
    }

    throw e
  }

  try {
    logger.debug("uploading dataset metadata")
    DatasetStore.putDatasetMeta(userID, datasetID, datasetMeta)
  } catch (e: Throwable) {
    logger.error("user dataset meta file upload to minio failed:", e)
    CacheDB().withTransaction { it.updateImportControl(datasetID, DatasetImportStatus.Failed) }
    throw e
  }

  if (uploadRefs.meta.isNotEmpty()) {
    logger.debug("uploading dataset meta files")
    try {
      TempFiles.withTempPath { docsZip ->
        if (uploadRefs.meta.size == 1)
          TempFiles.withTempDirectory { dir -> uploadRefs.meta[0].repack(into = docsZip, using = dir, logger = logger) }
        else
          DatasetStore.putImportReadyZip(userID, datasetID, docsZip::inputStream)
      }
    } catch (e: Throwable) {
      logger.error("dataset additional document file(s) upload to minio failed:", e)
      throw e
    }
  }
}

fun <T: ControllerBase> T.verifyFileSize(uploadRefs: UploadFileReferences, uploadConfig: UploadConfig) {
  val dataSize = uploadRefs.data.reduceTo(0L) { t, c -> t + c.fileSize() }
  val metaSize = uploadRefs.meta.reduceTo(0L) { t, c -> t + c.fileSize() }

  if (dataSize > uploadConfig.maxUploadSize.toLong())
    throw BadRequestException("upload upload size larger than the max permitted dataset size of "
    + uploadConfig.maxUploadSize.toString() + " bytes")

  val remainingUploadAllowance = getUserRemainingQuota(uploadConfig)

  if (dataSize + metaSize > remainingUploadAllowance)
    throw BadRequestException(
      "total upload size is larger than the remaining space allowed by the user quota " +
      "($remainingUploadAllowance bytes)")
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
private fun Path.repack(into: Path, using: Path, logger: Logger): List<DatasetFileInfo> {
  // If it resembles a zip file
  return if (name.endsWith(".zip")) {
    repackZip(into, using, logger)
  }

  // If it resembles a tar file
  else if (name.endsWith(".tar.gz") || name.endsWith(".tgz")) {
    repackTar(into, using, logger)
  } else {
    repackRaw(into, logger)
  }
}

/**
 * Packs the receiver files into a zip file for upload to S3.
 *
 * @receiver Paths to the files to pack.
 *
 * @param into Path to the new archive that should be created.
 */
private fun List<Path>.pack(into: Path): List<DatasetFileInfo> {
  into.compress(this)
  return map { DatasetFileInfo(it.name, it.fileSize().toULong()) }
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
private fun Path.repackZip(into: Path, using: Path, logger: Logger): List<DatasetFileInfo> {
  logger.trace("repacking zip file {} into {}", this, into)

  // Map of file names to sizes that will be stored in the postgres database.
  val files = ArrayList<DatasetFileInfo>(12)

  // Validate that the zip appears usable.
  validateZip()

  // List of paths for the unpacked files
  val unpacked = ArrayList<Path>(12)

  // Iterate through the zip entries
  try {
    this.zipEntries()
      .forEach { (entry, input) ->

        // If the zip entry contains a slash, we reject it (we don't presently
        // allow subdirectories)
        if (entry.name.contains('/') || entry.isDirectory) {
          // If the archive contains a __MACOSX directory, skip it silently.
          if (entry.name.startsWith("__MACOSX")) {
            return@forEach
          }

          throw BadRequestException("uploaded zip file must not contain subdirectories")
        }

        val tmpFile = using.resolve(entry.name)

        tmpFile.createFile()
        tmpFile.outputStream().use { out -> input.transferTo(out) }

        files.add(DatasetFileInfo(entry.name, tmpFile.fileSize().toULong()))

        unpacked.add(tmpFile)
      }
  } catch (_: IllegalStateException) {
    throw BadRequestException("decompressed file size is too large")
  }

  // ensure that the zip actually contained some files
  if (unpacked.isEmpty())
    throw BadRequestException("uploaded file was empty or was not a valid zip")

  logger.info("Compressing file from {} into {}", unpacked, into)
  // recompress the files as a tgz file
  into.compress(unpacked)

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
private fun Path.repackTar(into: Path, using: Path, logger: Logger): List<DatasetFileInfo> {
  logger.trace("repacking tar {} into {}", this, into)

  // Output map of files to sizes that will be written to the postgres DB.
  val sizes = ArrayList<DatasetFileInfo>(12)

  try {
    Tar.decompressWithGZip(this, using)
  } catch (_: ZipException) {
    throw BadRequestException("input file had gzipped tar extension but could not be packed as a gzipped tar")
  }

  val files = using.listDirectoryEntries()

  if (files.isEmpty())
    throw BadRequestException("uploaded file contained no files or was not a valid tar archive")

  for (file in files)
    sizes.add(DatasetFileInfo(file.name, file.fileSize().toULong()))

  into.compress(files)

  return sizes
}

private fun Path.repackRaw(into: Path, logger: Logger): List<DatasetFileInfo> {
  logger.trace("repacking raw file {} into {}", this, into)
  into.compress(listOf(this))
  return listOf(DatasetFileInfo(name, fileSize().toULong()))
}

private fun Path.validateZip() {
  when (getZipType()) {
    ZipType.Empty    -> throw BadRequestException("uploaded zip file is empty")
    ZipType.Standard -> { /* OK */
    }

    ZipType.Spanned  -> throw BadRequestException("uploaded zip file is part of a spanned archive")
    ZipType.Invalid  -> throw BadRequestException("uploaded zip file is invalid")
  }
}

// endregion Dataset File Processing

// region Dataset File Retrieval

@OptIn(ExperimentalPathApi::class)
fun <T: ControllerBase> T.downloadRemoteFile(url: URL, uploadConfig: UploadConfig, tmpDir: Path): Path {
  logger.info("attempting to download a remote file from {}", url)

  val response = try {
    url.fetchContent()
  } catch (e: URLFetchException) {
    throw FailedDependencyException(url.toString(), e.message, e)
  }

  // If the remote server "successfully" returned an error code or some other
  // non-2xx code.
  if (!response.isSuccess) {
    // If the user gave us an expired AWS URL (common for Nephele), then we can
    // give them a more informative error message.
    url.getAWSExpires()
      ?.format(DateFormat)
      ?.let { timestamp ->
        logger.debug("could not download remote file from \"{}\", url expired at {}", url.host, timestamp)
        throw FailedDependencyException(url, "remote server at \"${url.host}\" returned unexpected status code ${response.status}; given url appears to have expired at $timestamp")
      }

    logger.debug("could not download remote file from \"{}\", got status code {}", url, response.status)
    throw FailedDependencyException(url, "remote server at \"${url.host}\" returned unexpected status code ${response.status}")
  }

  // If for some reason the server returned nothing.
  if (!response.hasBody)
    throw FailedDependencyException(url, "remote server returned code ${response.status} with no content")

  val tmpFile = (response.fileName
    ?: url.safeFilename().takeUnless { it.isBlank() }
    ?: (url.host.replace('.', '_') + "_download"))
    .let { Path(it) }

  // Try to download the file from the source URL.
  try {
    val maxUploadSize = min(getUserRemainingQuota(uploadConfig), uploadConfig.maxUploadSize.toLong())

    BoundedInputStream(maxUploadSize, response.body!!) {
      val message = if (maxUploadSize < uploadConfig.maxUploadSize.toLong())
        "given source URL was to a file that exceeds the remaining upload space allowed by the user quota " +
        "($maxUploadSize bytes)"
      else
        "given source URL was to a file that exceeded the maximum allowed file size of $maxUploadSize bytes."

      BadRequestException(message)
    }.use { inp -> tmpFile.outputStream().use { out -> inp.transferTo(out) } }
  } catch (e: Throwable) {
    logger.error("content transfer from ${url.shortForm}", e)
    tmpFile.deleteIfExists()
    tmpDir.deleteRecursively()
    throw FailedDependencyException(
      url,
      "error occurred while attempting to download source file from ${url.shortForm}",
    )
  }

  return tmpFile
}

fun String.toURL() =
  try {
    toJavaURL()
  } catch (e: MalformedURLException) {
    throw BadRequestException("invalid file source: ${e.message}")
  }

private fun FailedDependencyException(url: URL, msg: String) = FailedDependencyException(url.toString(), msg)

private fun <T: ControllerBase> T.getUserRemainingQuota(uploadConfig: UploadConfig): Long =
  max(0L, uploadConfig.userMaxStorageSize.toLong() - getCurrentQuotaUsage(userID))

/**
 * Special handling for AWS urls that contain an Expires query parameter.  If
 * we get a 403 from AWS there is a good chance the link expired, in which case
 * we can give the user a more informative error message.
 */
private fun URL.getAWSExpires(): OffsetDateTime? {
  if (!host.endsWith("amazonaws.com"))
    return null

  val match = Regex("Expires=(\\d+)").find(query) ?: return null

  return Instant.ofEpochSecond(match.groupValues[1].toLongOrNull() ?: return null)
    .atOffset(ZoneOffset.UTC)
}

private fun URL.safeFilename(): String {
  val filename = path.substringAfterLast('/').trim()

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

    val idx1 = it.lastIndexOf('.', idx2 - 1)

    it.substring(idx1 + 1, idx2)
  } + "_download"
}

// endregion Dataset File Retrieval
