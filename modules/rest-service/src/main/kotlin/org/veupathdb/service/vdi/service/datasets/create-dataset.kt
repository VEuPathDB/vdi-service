package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.InternalServerErrorException
import org.slf4j.LoggerFactory
import org.veupathdb.service.vdi.db.internal.CacheDB
import org.veupathdb.service.vdi.db.internal.model.DatasetRecord
import org.veupathdb.service.vdi.generated.model.DatasetImportStatus
import org.veupathdb.service.vdi.generated.model.DatasetPostRequest
import org.veupathdb.service.vdi.s3.DatasetStore
import java.net.URL
import java.time.OffsetDateTime
import kotlin.io.path.deleteIfExists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream
import vdi.components.common.compression.Tar
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID
import vdi.components.common.util.Tmp
import vdi.components.common.util.useThenDelete

private val log = LoggerFactory.getLogger("create-dataset.kt")

fun createDataset(userID: UserID, datasetID: DatasetID, entity: DatasetPostRequest) {
  log.trace("createDataset(userID={}, datasetID={}, entity={})", userID, datasetID, entity)

  // Create a record for the dataset to be inserted into the database.
  val datasetRow = DatasetRecord(
    datasetID   = datasetID,
    typeName    = entity.meta.datasetType.name,
    typeVersion = entity.meta.datasetType.version,
    ownerID     = userID,
    isDeleted   = false,
    created     = OffsetDateTime.now(),
    name        = entity.meta.name,
    summary     = entity.meta.summary,
    description = entity.meta.description,
    files       = emptyList(),
    projects    = entity.meta.projects,
  )

  // Get a handle on the temp file that will be uploaded to the S3 store (MinIO)
  val tmpFile = if (entity.file != null) {
    // If the user uploaded a file, then use that
    entity.file.toPath()
  } else {
    // If the user gave us a URL then we have to download the contents of that
    // URL to a local file to be uploaded.   This is done to catch errors with
    // the URL or transfer before we start uploading to the dataset store.
    val path = Tmp.newPath()

    // Try to construct a URL instance (validating that the URL is sane)
    val url = try { URL(entity.url) }
    catch (e: Throwable) { throw BadRequestException("invalid source file URL given") }

    // Try to establish a connection to the URL target (validating that the
    // target is reachable)
    val connection = try { url.openConnection() }
    catch (e: Throwable) { throw BadRequestException("given source file URL was unreachable") }

    // Try to download the file from the source URL.
    try {
      connection.getInputStream()
        .use { inp -> path.outputStream().use { out -> inp.transferTo(out) } }
    } catch (e: Throwable) {
      log.error("failed to download file from target URL", e)
      path.deleteIfExists()
      throw InternalServerErrorException("error occurred while attempting to download source file from the given URL")
    }

    path
  }

  tmpFile.useThenDelete {
    val tarFile = Tmp.newPath()

    Tar.compressWithGZip(tarFile, listOf(tarFile))

    CacheDB.openTransaction()
      .use {
        try {
          it.insertDataset(datasetRow)
          it.insertDatasetMeta(datasetRow)
          it.insertDatasetProjects(datasetRow)
          it.insertImportControl(datasetID, DatasetImportStatus.AWAITINGIMPORT)

          DatasetStore.putUserUpload(datasetRow.ownerID, datasetID, tarFile::inputStream)
        } catch (e: Throwable) {
          it.rollback()
          log.error("postVdiDatasets failed!", e)
          throw InternalServerErrorException("upload failed")
        }
      }
  }

}