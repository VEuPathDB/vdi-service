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
import org.veupathdb.vdi.lib.common.compression.Tar
import org.veupathdb.vdi.lib.common.compression.Zip
import org.veupathdb.vdi.lib.common.compression.Zip.getZipType
import org.veupathdb.vdi.lib.common.compression.ZipType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.fs.TempFiles
import org.veupathdb.vdi.lib.common.fs.useThenDelete
import org.veupathdb.vdi.lib.common.model.*
import org.veupathdb.vdi.lib.handler.mapping.PluginHandlers
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.math.max

private val log = LoggerFactory.getLogger("create-dataset.kt")

fun createDataset(userID: UserID, datasetID: DatasetID, entity: DatasetPostRequest) {
  log.trace("createDataset(userID={}, datasetID={}, entity={})", userID, datasetID, entity)

  val datasetMeta = entity.toDatasetMeta(userID)

  val handler = PluginHandlers.get(datasetMeta.type.name, datasetMeta.type.version)
    ?: throw BadRequestException("target dataset type is unknown to the VDI service")

  for (projectID in datasetMeta.projects) {
    if (!handler.appliesToProject(projectID))
      throw BadRequestException("target dataset type does not apply to project $projectID")
  }

  // Get a handle on the temp file that will be uploaded to the S3 store (MinIO)
  TempFiles.withTempDirectory { directory ->
    TempFiles.withTempPath { archive ->
      entity.getDatasetFile()
        .useThenDelete { rawUpload ->
          verifyFileSize(rawUpload, userID)

          rawUpload.repack(into = archive, using = directory)

          log.debug("uploading raw user data to S3 for new dataset {} by user {}", datasetID, userID)
          DatasetStore.putUserUpload(userID, datasetID, archive::inputStream)
        }
    }
  }

  log.debug("uploading dataset metadata to S3 for new dataset {} by user {}", datasetID, userID)
  DatasetStore.putDatasetMeta(userID, datasetID, datasetMeta)
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
      name    = meta.datasetType.name,
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
    dependencies = (meta.dependencies ?: emptyList()).map {
      VDIDatasetDependencyImpl(
        identifier  = it.resourceIdentifier,
        version     = it.resourceVersion,
        displayName = it.resourceDisplayName
      )
    },
  )

private fun Path.repack(into: Path, using: Path) {
  // If it resembles a zip file
  if (name.endsWith(".zip")) {

    // Validate that the zip appears usable.
    validateZip()

    // List of paths for the unpacked files
    val unpacked = ArrayList<Path>(12)

    // Iterate through the zip entries
    Zip.zipEntries(this)
      .forEach { (entry, input) ->

        // If the zip entry contains a slash, we reject it (we don't presently allow subdirectories)
        if (entry.name.contains('/') || entry.isDirectory)
          throw BadRequestException("uploaded zip file must not contain subdirectories")

        val tmpFile = using.resolve(entry.name)

        tmpFile.createFile()
        tmpFile.outputStream().use { out -> input.transferTo(out) }

        unpacked.add(tmpFile)
      }

    // ensure that the zip actually contained some files
    if (unpacked.isEmpty())
      throw BadRequestException("uploaded zip file contains no files")

    log.info("Compressing file from {} into {}", unpacked, into)
    // recompress the files as a tgz file
    Tar.compressWithGZip(into, unpacked)
  }

  // If it resembles a tar file
  else if (name.endsWith(".tar.gz") || name.endsWith(".tgz")) {
    Tar.decompressWithGZip(this, using)

    val files = using.listDirectoryEntries()

    if (files.isEmpty())
      throw BadRequestException("uploaded tar file contains no files")

    Tar.compressWithGZip(into, files)
  }

  else {
    Tar.compressWithGZip(into, listOf(this))
  }
}

private fun DatasetPostRequest.getDatasetFile(): Path =
  if (file != null) {
    // If the user uploaded a file, then use that
    file.toPath()
  } else {
    // If the user gave us a URL then we have to download the contents of that
    // URL to a local file to be uploaded.   This is done to catch errors with
    // the URL or transfer before we start uploading to the dataset store.
    val path = TempFiles.makeTempPath()

    // Try to construct a URL instance (validating that the URL is sane)
    val url = try { URL(url) }
    catch (e: Throwable) { throw BadRequestException("invalid source file URL given") }

    // Try to establish a connection to the URL target (validating that the
    // target is reachable)
    val connection = try { url.openConnection() }
    catch (e: Throwable) { throw BadRequestException("given source file URL was unreachable") }

    // Try to download the file from the source URL.
    try {
      BoundedInputStream(JaxRSMultipartUpload.maxFileUploadSize, connection.getInputStream()) {
        BadRequestException("given source file URL pointed to a file that exceeded the max allowed upload size of ${JaxRSMultipartUpload.maxFileUploadSize} bytes.")
      }
        .use { inp -> path.outputStream().use { out -> inp.transferTo(out) } }
    } catch (e: Throwable) {
      log.error("failed to download file from target URL", e)
      path.deleteIfExists()
      throw InternalServerErrorException("error occurred while attempting to download source file from the given URL")
    }

    path
  }

private fun Path.validateZip() {
  when (getZipType()) {
    ZipType.Empty    -> throw BadRequestException("uploaded zip file is empty")
    ZipType.Standard -> { /* OK */ }
    ZipType.Spanned  -> throw BadRequestException("uploaded zip file is part of a spanned archive")
    ZipType.Invalid  -> throw BadRequestException("uploaded zip file is invalid")
  }
}
