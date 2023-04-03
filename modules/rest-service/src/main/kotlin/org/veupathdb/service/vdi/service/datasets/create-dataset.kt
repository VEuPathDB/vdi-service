package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.InternalServerErrorException
import org.slf4j.LoggerFactory
import org.veupathdb.lib.jaxrs.raml.multipart.JaxRSMultipartUpload
import org.veupathdb.service.vdi.generated.model.DatasetPostRequest
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.service.vdi.util.BoundedInputStream
import org.veupathdb.vdi.lib.common.compression.Tar
import org.veupathdb.vdi.lib.common.compression.Zip.getZipType
import org.veupathdb.vdi.lib.common.compression.ZipType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.fs.TempFiles
import org.veupathdb.vdi.lib.common.fs.useThenDelete
import org.veupathdb.vdi.lib.common.model.VDIDatasetDependency
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.DatasetMetaImpl
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecordImpl
import java.net.URL
import java.nio.file.Path
import java.time.OffsetDateTime
import kotlin.io.path.deleteIfExists
import kotlin.io.path.inputStream
import kotlin.io.path.name
import kotlin.io.path.outputStream

private val log = LoggerFactory.getLogger("create-dataset.kt")

fun createDataset(userID: UserID, datasetID: DatasetID, entity: DatasetPostRequest) {
  log.trace("createDataset(userID={}, datasetID={}, entity={})", userID, datasetID, entity)

  // Create a record for the dataset to be inserted into the database.
  val datasetMeta = VDIDatasetMeta(
    type         = VDIDatasetType(
      name    = entity.meta.datasetType.name,
      version = entity.meta.datasetType.version,
    ),
    projects     = entity.meta.projects.toSet(),
    owner        = userID.toString(),
    name         = entity.meta.name,
    summary      = entity.meta.summary,
    description  = entity.meta.description,
    dependencies = entity.meta.dependencies.map {
      VDIDatasetDependency(
        identifier  = it.resourceIdentifier,
        version     = it.resourceVersion,
        displayName = it.resourceDisplayName
      )
    },
  )

  log.debug("uploading dataset metadata to S3 for new dataset {} by user {}", datasetID, userID)
  DatasetStore.putDatasetMeta(userID, datasetID, datasetMeta)

  // Get a handle on the temp file that will be uploaded to the S3 store (MinIO)
  TempFiles.withTempPath { archive ->
    entity.getDatasetFile()
      .useThenDelete { rawUpload ->
        rawUpload.repack(into = archive)

        log.debug("uploading raw user data to S3 for new dataset {} by user {}", datasetID, userID)
        DatasetStore.putUserUpload(userID, datasetID, archive::inputStream)
      }
  }
}

private fun Path.repack(into: Path) {
  if (name.endsWith(".zip")) {
    when (getZipType()) {
      ZipType.Empty -> throw BadRequestException("uploaded zip file is empty")

      ZipType.Spanned -> throw BadRequestException("uploaded zip file is part of a set of spanned zip files")

      ZipType.Standard -> {
        val unzippedFiles: Collection<Path> = TODO("unzip")

        if (unzippedFiles.isEmpty())
          throw BadRequestException("uploaded zip file contains no files")

        Tar.compressWithGZip(into, unzippedFiles)
      }

      ZipType.Invalid -> throw BadRequestException("uploaded zip file is invalid")
    }
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