package vdi.service.rest.server.services.dataset

import org.veupathdb.lib.container.jaxrs.errors.FailedDependencyException
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.fs.TempFiles
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevision
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevisionAction
import java.time.OffsetDateTime
import vdi.lib.db.cache.CacheDB
import vdi.lib.db.cache.model.DatasetImportStatus
import vdi.lib.db.cache.withTransaction
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.DatasetPatchRequestBodyImpl
import vdi.service.rest.generated.model.DatasetPutRequestBody
import vdi.service.rest.generated.model.DatasetPutResponseBody
import vdi.service.rest.generated.resources.DatasetsVdiId.PutDatasetsByVdiIdResponse
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.inputs.cleanup
import vdi.service.rest.server.inputs.validate
import vdi.service.rest.server.outputs.ForbiddenError
import vdi.service.rest.server.outputs.Static404
import vdi.service.rest.server.outputs.UnprocessableEntityError
import vdi.service.rest.server.outputs.wrap
import vdi.service.rest.util.Either

internal fun <T: ControllerBase> T.putDataset(
  datasetID:    DatasetID,
  request:      DatasetPutRequestBody,
  uploadConfig: UploadConfig,
): Either<DatasetPutResponseBody, PutDatasetsByVdiIdResponse> {
  // Ensure the dataset exists and is owned by the requesting user.
  val originalDataset = CacheDB().selectDataset(datasetID)
    ?.takeIf { it.ownerID == userID }
    ?: return Either.ofRight(Static404.wrap())

  if (originalDataset.isDeleted)
    return Either.ofRight(ForbiddenError("cannot add revisions to a deleted dataset").wrap())

  request.cleanup()
  request.validate(originalDataset.projects)
    .takeUnless { it.isEmpty }
    ?.let { return Either.ofRight(UnprocessableEntityError(it).wrap()) }

  // validate type change
  val targetType = (request as DatasetPatchRequestBodyImpl).optValidateType {
    return Either.ofRight(ForbiddenError("cannot change the type of ${it.displayName} datasets").wrap())
  }

  // add .{inc} to dataset id to create new id
  val newDatasetID = datasetID.incrementRevision()

  val meta = DatasetStore.getDatasetMeta(userID, datasetID)
    // apply meta changes
    ?.let { it.applyPatch(
      userID          = userID,
      targetType      = targetType,
      patch           = request.meta,
      originalID      = datasetID,
      revisionHistory = it.revisionHistory + VDIDatasetRevision(
        action       = VDIDatasetRevisionAction.Revise,
        timestamp    = OffsetDateTime.now(),
        revisionID   = newDatasetID,
        revisionNote = request.meta.revisionNote,
      ),
    ) }
    ?: throw IllegalStateException("target dataset has no $DatasetMetaFilename file")

  val (tempDirectory, uploadFile) = CacheDB().initializeDataset(userID, newDatasetID, meta) {
    try {
      fetchDatasetFile(request, uploadConfig)
    } catch (e: Throwable) {
      CacheDB().withTransaction { t ->
        t.updateImportControl(datasetID, DatasetImportStatus.Failed)
        if (e is FailedDependencyException && e.message != null)
          t.tryInsertImportMessages(datasetID, e.message!!)
      }
      throw e
    }
  }

  submitUpload(newDatasetID, tempDirectory, uploadFile, meta, uploadConfig)

  return Either.ofLeft(vdi.service.rest.generated.model.DatasetPutResponseBodyImpl().apply { datasetId = newDatasetID.toString() })
}

/**
 * Increments the dataset revision indicator suffix if present, or appends the
 * suffix `.1` if no suffix previously existed.
 */
private fun DatasetID.incrementRevision() =
  toString().let { raw ->
    raw.indexOf('.')
      .takeUnless { it == -1 }
      ?.let { it+1 }
      ?.let { DatasetID(raw.substring(0, it) + (raw.substring(it).toInt() + 1)) }
      ?: DatasetID("$raw.1")
  }

private fun <T: ControllerBase> T.fetchDatasetFile(body: DatasetPutRequestBody, uploadConfig: UploadConfig): FileReference =
  body.file?.let {
    TempFiles.makeTempPath(it.name)
      .also { (_, tmpFile) -> it.copyTo(tmpFile.toFile(), true) }
      .let { (tmpDir, tmpFile) -> FileReference(tmpDir, tmpFile) }
  } ?: downloadRemoteFile(body.url.toURL(), uploadConfig)
