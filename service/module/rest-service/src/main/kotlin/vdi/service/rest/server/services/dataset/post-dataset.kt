package vdi.service.rest.server.services.dataset

import org.veupathdb.lib.container.jaxrs.errors.FailedDependencyException
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.fs.TempFiles
import vdi.lib.db.cache.CacheDB
import vdi.lib.db.cache.model.DatasetImportStatus
import vdi.lib.db.cache.withTransaction
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.DatasetPostRequestBody
import vdi.service.rest.generated.model.DatasetProxyPostRequestBody
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.inputs.toDatasetMeta

fun <T: ControllerBase> T.createDataset(datasetID: DatasetID, entity: DatasetPostRequestBody, uploadConfig: UploadConfig) {
  val datasetMeta = entity.toDatasetMeta(userID)

  // IMPORTANT!!!
  //
  // We need our own copy of the upload file to process the upload
  // asynchronously.  If we use the upload file managed by ContainerCore, that
  // file will be deleted out from under us.
  val (tempDirectory, uploadFile) = CacheDB().initializeDataset(userID, datasetID, datasetMeta) {
    try {
      fetchDatasetFile(entity, uploadConfig)
    } catch (e: Throwable) {
      CacheDB().withTransaction { t ->
        t.updateImportControl(datasetID, DatasetImportStatus.Failed)
        if (e is FailedDependencyException && e.message != null)
          t.tryInsertImportMessages(datasetID, e.message!!)
      }
      throw e
    }
  }

  submitUpload(datasetID, tempDirectory, uploadFile, datasetMeta, uploadConfig)
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
 * @return A data object containing the temp directory path and the temp file
 * path for the dataset input file.
 */
private fun <T: ControllerBase> T.fetchDatasetFile(body: DatasetPostRequestBody, uploadConfig: UploadConfig): FileReference =
  body.file?.let {
    TempFiles.makeTempPath(it.name)
      .also { (_, tmpFile) -> it.copyTo(tmpFile.toFile(), true) }
      .let { (tmpDir, tmpFile) -> FileReference(tmpDir, tmpFile) }
  } ?: downloadRemoteFile(body.url.toURL(), uploadConfig)

fun <T: ControllerBase> T.createDataset(datasetID: DatasetID, entity: DatasetProxyPostRequestBody, uploadConfig: UploadConfig) {
  val datasetMeta = entity.toDatasetMeta(userID)

  // IMPORTANT!!!
  //
  // We need our own copy of the upload file to process the upload
  // asynchronously.  If we use the upload file managed by ContainerCore, that
  // file will be deleted out from under us.
  val (tempDirectory, uploadFile) = CacheDB().initializeDataset(userID, datasetID, datasetMeta) {
    try {
      fetchDatasetFile(entity, uploadConfig)
    } catch (e: Throwable) {
      CacheDB().withTransaction { t ->
        t.updateImportControl(datasetID, DatasetImportStatus.Failed)
        if (e is FailedDependencyException && e.message != null)
          t.tryInsertImportMessages(datasetID, e.message!!)
      }
      throw e
    }
  }

  submitUpload(datasetID, tempDirectory, uploadFile, datasetMeta, uploadConfig)
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
 * @return A data object containing the temp directory path and the temp file
 * path for the dataset input file.
 */
private fun <T: ControllerBase> T.fetchDatasetFile(body: DatasetProxyPostRequestBody, uploadConfig: UploadConfig): FileReference =
  body.file?.let {
    TempFiles.makeTempPath(it.name)
      .also { (_, tmpFile) -> it.copyTo(tmpFile.toFile(), true) }
      .let { (tmpDir, tmpFile) -> FileReference(tmpDir, tmpFile) }
  } ?: downloadRemoteFile(body.url.toURL(), uploadConfig)
