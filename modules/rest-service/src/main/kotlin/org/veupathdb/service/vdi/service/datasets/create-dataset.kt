package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.InternalServerErrorException
import org.slf4j.LoggerFactory
import org.veupathdb.lib.jaxrs.raml.multipart.JaxRSMultipartUpload
import org.veupathdb.service.vdi.generated.model.DatasetPostRequest
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.service.vdi.util.BoundedInputStream
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.fs.Tmp
import org.veupathdb.vdi.lib.common.fs.useThenDelete
import org.veupathdb.vdi.lib.db.cache.OldCacheDB
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecordImpl
import java.net.URL
import java.time.OffsetDateTime
import kotlin.io.path.deleteIfExists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream
import vdi.components.compression.Tar

private val log = LoggerFactory.getLogger("create-dataset.kt")

fun createDataset(userID: UserID, datasetID: DatasetID, entity: DatasetPostRequest) {
  log.trace("createDataset(userID={}, datasetID={}, entity={})", userID, datasetID, entity)

  // Create a record for the dataset to be inserted into the database.
  val datasetRow = DatasetRecordImpl(
    datasetID   = datasetID,
    typeName    = entity.meta.datasetType.name,
    typeVersion = entity.meta.datasetType.version,
    ownerID     = userID,
    isDeleted   = false,
    created     = OffsetDateTime.now(),
    importStatus = org.veupathdb.vdi.lib.db.cache.model.DatasetImportStatus.AwaitingImport,
    name        = entity.meta.name,
    summary     = entity.meta.summary,
    description = entity.meta.description,
    files       = emptyList(),
    projects    = entity.meta.projects,
  )

  // Get a handle on the temp file that will be uploaded to the S3 store (MinIO)
  val tmpFile =

  // TODO: what if the input file is a zip or tar file itself?

  // With the temp file (deleting after use)
  entity.getDatasetFile()
    .useThenDelete { tmpFile ->
      // with a new temp path for our tar file (deleting after user)
      Tmp.withPath { tarFile ->

        Tar.compressWithGZip(tarFile, listOf(tmpFile))

        OldCacheDB.openTransaction()
          .use {
            try {
              it.insertDataset(datasetRow)
              it.insertDatasetMeta(datasetRow)
              it.insertDatasetProjects(datasetRow)
              it.insertImportControl(datasetID, datasetRow.importStatus)

              DatasetStore.putUserUpload(datasetRow.ownerID, datasetID, tarFile::inputStream)
            } catch (e: Throwable) {
              it.rollback()
              log.error("postVdiDatasets failed!", e)
              throw InternalServerErrorException("upload failed")
            }
          }
      }
    }
}

private fun DatasetPostRequest.getDatasetFile() =
  if (file != null) {
    // If the user uploaded a file, then use that
    file.toPath()
  } else {
    // If the user gave us a URL then we have to download the contents of that
    // URL to a local file to be uploaded.   This is done to catch errors with
    // the URL or transfer before we start uploading to the dataset store.
    val path = Tmp.newPath()

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