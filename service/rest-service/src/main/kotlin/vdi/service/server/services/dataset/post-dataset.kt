package vdi.service.server.services.dataset

import org.veupathdb.lib.container.jaxrs.errors.FailedDependencyException
import vdi.service.generated.model.DatasetPostRequestBody
import vdi.service.server.inputs.toDatasetMeta
import vdi.service.util.*
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.fs.TempFiles
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.db.cache.withTransaction
import vdi.lib.logging.logger
import kotlin.io.path.*

fun <T: Any> T.createDataset(
  userID: UserID,
  datasetID: DatasetID,
  entity: DatasetPostRequestBody,
) {
  val datasetMeta = entity.toDatasetMeta(userID)

  // IMPORTANT!!!
  //
  // We need our own copy of the upload file to process the upload
  // asynchronously.  If we use the upload file managed by ContainerCore, that
  // file will be deleted out from under us.
  val (tempDirectory, uploadFile) = CacheDB().initializeDataset(userID, datasetID, datasetMeta) {
    try {
      entity.fetchDatasetFile(userID)
    } catch (e: Throwable) {
      CacheDB().withTransaction { t ->
        t.updateImportControl(datasetID, DatasetImportStatus.Failed)
        if (e is FailedDependencyException && e.message != null)
          t.tryInsertImportMessages(datasetID, e.message!!)
      }
      throw e
    }
  }

  submitUpload(userID, datasetID, tempDirectory, uploadFile, datasetMeta)
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
 * @param userID ID of the user attempting to upload a dataset.
 *
 * @param logger Logger attached to the controller class handling the request.
 *
 * @return A data object containing the temp directory path and the temp file
 * path for the dataset input file.
 */
private fun DatasetPostRequestBody.fetchDatasetFile(userID: UserID): FileReference =
  file?.let {
    TempFiles.makeTempPath(it.name)
      .also { (_, tmpFile) -> it.copyTo(tmpFile.toFile(), true) }
      .let { (tmpDir, tmpFile) -> FileReference(tmpDir, tmpFile) }
  } ?: downloadRemoteFile(url.toURL(), userID)
