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
import vdi.core.plugin.registry.PluginDatasetTypeMeta
import vdi.core.plugin.registry.PluginRegistry
import vdi.logging.mark
import vdi.model.OriginTimestamp
import vdi.model.meta.*
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.BadRequestError
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.outputs.BadRequestError
import vdi.service.rest.server.services.users.getCurrentQuotaUsage
import vdi.service.rest.util.*
import vdi.util.fn.Either
import vdi.util.fn.leftOr
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
    it.tryInsertDataset(
      DatasetImpl(
        datasetID    = datasetID,
        type         = datasetMeta.type,
        ownerID      = userID,
        isDeleted    = false,
        created      = datasetMeta.created,
        importStatus = DatasetImportStatus.Queued,
        origin       = datasetMeta.origin,
        inserted     = OffsetDateTime.now(),
      )
    )
    it.tryInsertDatasetMeta(datasetID, datasetMeta)
    it.tryInsertImportControl(datasetID, DatasetImportStatus.Queued)
    it.tryInsertDatasetProjects(datasetID, datasetMeta.installTargets)
    it.tryInsertSyncControl(
      SyncControlRecord(
        datasetID     = datasetID,
        sharesUpdated = OriginTimestamp,
        dataUpdated   = OriginTimestamp,
        metaUpdated   = OriginTimestamp
      )
    )

    fn(it)
  }

// endregion Cache DB Interactions

// region Dataset File Processing

fun verifyFileExtensions(data: Collection<File>, dataType: DatasetType): BadRequestError? {
  val exts = PluginRegistry.require(dataType).allowedFileExtensions

  // If no special extensions have been defined, we can't validate.
  if (exts.isEmpty())
    return null

  for (file in data) {
    val name = file.name.lowercase()

    for (e in SupportedArchiveType.entries)
      if (e.matches(name))
        return null

    for (e in exts)
      if (name.endsWith(e))
        return null
  }

  return BadRequestError(
    "unsupported data file type.  permitted upload data file types are: " +
    (SupportedArchiveType.SupportedExtensions + exts).joinToString("', '", "['", "']")
  )
}

@OptIn(ExperimentalPathApi::class)
fun ControllerBase.resolveDatasetFiles(
  data:         Iterable<File>?,
  url:          String?,
  meta:         Iterable<File>?,
  properties:   Iterable<File>?,
  uploadConfig: UploadConfig,
): UploadFileReferences {
  logger.debug("resolving dataset files")

  val tmpDir = TempFiles.makeTempDirectory()

  return try {
    if (data != null)
      resolveUploadFiles(
        data.asSequence(),
        meta?.asSequence() ?: emptySequence(),
        properties?.asSequence() ?: emptySequence(),
        tmpDir,
      )
    else
      resolveURLFile(
        url!!,
        meta?.asSequence() ?: emptySequence(),
        properties?.asSequence() ?: emptySequence(),
        uploadConfig,
        tmpDir,
      )
  } catch (e: Throwable) {
    tmpDir.deleteRecursively()
    throw e
  }
}

fun resolveUploadFiles(
  data:   Sequence<File>,
  docs:   Sequence<File>,
  props:  Sequence<File>,
  tmpDir: Path,
): UploadFileReferences =
  UploadFileReferences(
    tempDir = tmpDir,
    data    = data.toTempPaths(tmpDir),
    docs    = docs.toTempPaths(tmpDir),
    props   = props.toTempPaths(tmpDir),
  )

fun ControllerBase.resolveURLFile(
  url:          String,
  docs:         Sequence<File>,
  props:        Sequence<File>,
  uploadConfig: UploadConfig,
  tmpDir:       Path,
): UploadFileReferences =
  UploadFileReferences(
    tempDir = tmpDir,
    data    = listOf(downloadRemoteFile(url.toURL(), uploadConfig, tmpDir)),
    docs    = docs.toTempPaths(tmpDir),
    props   = props.toTempPaths(tmpDir)
  )

data class UploadFileReferences(
  val tempDir: Path,
  val data:    List<Path>,
  val docs:    List<Path>,
  val props:   List<Path>,
)

private fun Sequence<File>.toTempPaths(tmpDir: Path): List<Path> =
  map { it.copyTo(tmpDir.resolve(it.name).toFile()) }
    .map { it.toPath() }
    .toList()

private val WorkPool = Executors.newFixedThreadPool(10)

@OptIn(ExperimentalPathApi::class)
fun ControllerBase.submitUpload(
  datasetID:    DatasetID,
  uploadRefs:   UploadFileReferences,
  datasetMeta:  DatasetMetadata,
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

fun ControllerBase.uploadFiles(
  datasetID:    DatasetID,
  uploadRefs:   UploadFileReferences,
  datasetMeta:  DatasetMetadata,
  uploadConfig: UploadConfig,
) {
  val logger = logger.mark(userID, datasetID)

  try {
    logger.debug("uploading dataset metadata to object store")
    DatasetStore.putDatasetMeta(userID, datasetID, datasetMeta)
  } catch (e: Throwable) {
    logger.error("user dataset meta file upload to object store failed:", e)
    CacheDB().withTransaction { it.updateImportControl(datasetID, DatasetImportStatus.Failed) }
    throw e
  }

  logger.debug("checking upload file sizes")
  verifyUploadFileSize(uploadRefs, uploadConfig)

  try {
    TempFiles.withTempPath { archive ->
      val sizes = if (uploadRefs.data.size == 1) {
        logger.debug("input was a single file, attempting to (re)pack")
        TempFiles.withTempDirectory { dir ->
          context(logger) {
            uploadRefs.data[0].repack(
              into         = archive,
              using        = dir,
              dataTypeMeta = PluginRegistry.require(datasetMeta.type),
              uploadConfig = uploadConfig,
            )
          }
        }
          .leftOr { throw BadRequestException(it.message) }
      } else {
        uploadRefs.data.pack(into = archive)
      }

      logger.debug("uploading manifest to object store")
      DatasetStore.putManifest(userID, datasetID, DatasetManifest(sizes, emptyList()))

      logger.debug("uploading raw dataset data to object store")
      DatasetStore.putImportReadyZip(userID, datasetID, archive::inputStream)

      CacheDB().withTransaction { it.tryInsertUploadFiles(datasetID, sizes) }
    }
  } catch (e: Throwable) {
    if (e is WebApplicationException && (e.response?.status ?: 500) in 400..499) {
      logger.info("rejecting dataset upload for user error: {}", e.message)
      DatasetStore.putUploadError(userID, datasetID, e.message!!)
    } else {
      logger.error("user dataset upload to object store failed: ", e)
      DatasetStore.putUploadError(userID, datasetID, "internal server error", e)
      Metrics.Upload.failed.inc()
      CacheDB().withTransaction { it.updateImportControl(datasetID, DatasetImportStatus.Failed) }
    }

    throw e
  }

  if (uploadRefs.docs.isNotEmpty()) {
    logger.debug("uploading dataset meta files")
    try {
      for (path in uploadRefs.docs)
        DatasetStore.putDocumentFile(userID, datasetID, path.name, path::inputStream)
    } catch (e: Throwable) {
      logger.error("dataset additional document file(s) upload to object-store failed:", e)
      throw e
    }
  }

  if (uploadRefs.props.isNotEmpty()) {
    logger.debug("uploading data properties files")
    try {
      for (path in uploadRefs.props)
        DatasetStore.putVariablePropertiesFile(userID, datasetID, path.name, path::inputStream)
    } catch (e: Throwable) {
      logger.error("dataset additional document file(s) upload to object-store failed:", e)
      throw e
    }
  }
}

fun ControllerBase.verifyUploadFileSize(
  uploadRefs:   UploadFileReferences,
  uploadConfig: UploadConfig,
) {
  val dataSize = uploadRefs.data.reduceTo(0L) { t, c -> t + c.fileSize() }
  val metaSize = uploadRefs.docs.reduceTo(0L) { t, c -> t + c.fileSize() }
  val propSize = uploadRefs.props.reduceTo(0L) { t, c -> t + c.fileSize() }

  if (dataSize > uploadConfig.maxUploadSize.toLong())
    throw BadRequestException(
      "upload upload size larger than the max permitted dataset size of "
      + uploadConfig.maxUploadSize.toFileSizeString()
    )

  val remainingUploadAllowance = getUserRemainingQuota(uploadConfig)
  val totalUploadSize = dataSize + metaSize + propSize

  if (totalUploadSize > remainingUploadAllowance)
    throw BadRequestException("total upload size is larger than the remaining" +
      " space allowed by the user quota (${remainingUploadAllowance.toFileSizeString()})")
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
context(logger: Logger, controller: ControllerBase)
private fun Path.repack(
  into:         Path,
  using:        Path,
  dataTypeMeta: PluginDatasetTypeMeta,
  uploadConfig: UploadConfig,
): Either<List<DatasetFileInfo>, BadRequestError> =
  when {
    SupportedArchiveType.Zip.matches(name) -> {
      validateZip(dataTypeMeta, controller.getUserRemainingQuota(uploadConfig))
        ?.let { Either.right(it) }
        ?: Either.left(repackZip(into, using))
    }

    SupportedArchiveType.TarGZ.matches(name) -> {
      validateTar(dataTypeMeta, controller.getUserRemainingQuota(uploadConfig))
        ?.let { Either.right(it) }
        ?: Either.left(repackTar(into, using))
    }

    else -> repackRaw(into, dataTypeMeta)
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
context(logger: Logger)
private fun Path.repackZip(into: Path, using: Path): List<DatasetFileInfo> {
  logger.trace("repacking zip file {} into {}", this, into)

  // Map of file names to sizes that will be stored in the postgres database.
  val files = ArrayList<DatasetFileInfo>(12)

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
context(logger: Logger)
private fun Path.repackTar(into: Path, using: Path): List<DatasetFileInfo> {
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

private fun Path.validateTar(dataTypeMeta: PluginDatasetTypeMeta, remainingQuota: Long): BadRequestError? {
  val maxSize = dataTypeMeta.maxFileSize.toLong()
  val messages = ArrayList<String>(2)
  var totalSize = 0L

  Tar.entries(this, true).forEach { (name, size) ->
    if (!dataTypeMeta.allowedFileExtensions.matchesFilename(name))
      messages.add("archive file \"$name\" has an unrecognized or disallowed file extension")

    if (size > maxSize)
      messages.add("archive file \"$name\" size is larger than the max permitted size of ${maxSize.toFileSizeString()}")

    totalSize += size
  }

  if (totalSize > remainingQuota)
    messages.add("total size of archive content is larger than the remaining" +
    " user allowance of ${remainingQuota.toFileSizeString()}")

  return messages.takeUnless { it.isEmpty() }
    ?.let { BadRequestError(it.joinToString("\n")) }
}

context(logger: Logger)
private fun Path.repackRaw(into: Path, dataTypeMeta: PluginDatasetTypeMeta): Either<List<DatasetFileInfo>, BadRequestError> {
  logger.trace("packing raw file {} into {}", this, into)

  if (fileSize() > dataTypeMeta.maxFileSize.toLong())
    return Either.right(BadRequestError("uploaded file larger than the max permitted size of ${dataTypeMeta.maxFileSize.toFileSizeString()}"))

  if (!dataTypeMeta.allowedFileExtensions.matchesFilename(name.lowercase()))
    return Either.right(BadRequestError("uploaded file has an unrecognized or disallowed file extension"))

  into.compress(listOf(this))

  return Either.left(listOf(DatasetFileInfo(name, fileSize().toULong())))
}

private fun Path.validateZip(dataTypeMeta: PluginDatasetTypeMeta, remainingQuota: Long): BadRequestError? {
  when (getZipType()) {
    ZipType.Empty    -> return BadRequestError("uploaded zip file is empty")
    ZipType.Standard -> { /* OK */
    }

    ZipType.Spanned  -> return BadRequestError("uploaded zip file is part of a spanned archive")
    ZipType.Invalid  -> return BadRequestError("uploaded zip file is invalid")
  }

  val maxSize = dataTypeMeta.maxFileSize.toLong()
  val messages = ArrayList<String>(2)
  var totalSize = 0L

  zipHeaders().forEach { file ->
    if (!dataTypeMeta.allowedFileExtensions.matchesFilename(file.name))
      messages.add("archive contains data file \"${file.name}\" with an" +
        " unrecognized or disallowed file extension")

    if (file.size > maxSize)
      messages.add("archive contains data file \"${file.name}\" with a size" +
        " larger than the max permitted size of ${maxSize.toFileSizeString()}")

    totalSize += file.size
  }

  if (totalSize > remainingQuota)
    messages.add("total size of archive content is larger than the remaining" +
      " user allowance of ${remainingQuota.toFileSizeString()}")


  if (messages.isNotEmpty())
    return BadRequestError(messages.joinToString("\n"))

  return null
}

private fun Array<String>.matchesFilename(name: String) =
  isEmpty() || any(name.lowercase()::endsWith)

// endregion Dataset File Processing

// region Dataset File Retrieval

@OptIn(ExperimentalPathApi::class)
fun ControllerBase.downloadRemoteFile(url: URL, uploadConfig: UploadConfig, tmpDir: Path): Path {
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
        throw FailedDependencyException(
          url,
          "remote server at \"${url.host}\" returned unexpected status code ${response.status}; given url appears to have expired at $timestamp"
        )
      }

    logger.debug("could not download remote file from \"{}\", got status code {}", url, response.status)
    throw FailedDependencyException(
      url,
      "remote server at \"${url.host}\" returned unexpected status code ${response.status}"
    )
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

private fun ControllerBase.getUserRemainingQuota(uploadConfig: UploadConfig): Long =
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
