package vdi.service.server.services.dataset

import org.veupathdb.lib.container.jaxrs.errors.FailedDependencyException
import vdi.service.generated.model.DatasetPatchRequestBodyImpl
import vdi.service.generated.model.DatasetPutRequestBody
import vdi.service.generated.model.DatasetPutResponseBody
import vdi.service.generated.model.DatasetPutResponseBodyImpl
import vdi.service.generated.resources.DatasetsVdiId.PutDatasetsByVdiIdResponse
import vdi.service.s3.DatasetStore
import vdi.service.server.inputs.cleanup
import vdi.service.server.inputs.validate
import vdi.service.server.outputs.ForbiddenError
import vdi.service.server.outputs.Static404
import vdi.service.server.outputs.UnprocessableEntityError
import vdi.service.server.outputs.wrap
import vdi.service.util.Either
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.fs.TempFiles
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevision
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevisionAction
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.db.cache.withTransaction
import java.time.OffsetDateTime

internal fun putDataset(
  userID:    UserID,
  datasetID: DatasetID,
  request:   DatasetPutRequestBody,
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
      request.fetchDatasetFile(userID)
    } catch (e: Throwable) {
      CacheDB().withTransaction { t ->
        t.updateImportControl(datasetID, DatasetImportStatus.Failed)
        if (e is FailedDependencyException && e.message != null)
          t.tryInsertImportMessages(datasetID, e.message!!)
      }
      throw e
    }
  }

  submitUpload(userID, newDatasetID, tempDirectory, uploadFile, meta)

  return Either.ofLeft(DatasetPutResponseBodyImpl().apply { datasetId = newDatasetID.toString() })
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

private fun DatasetPutRequestBody.fetchDatasetFile(userID: UserID): FileReference =
  file?.let {
    TempFiles.makeTempPath(it.name)
      .also { (_, tmpFile) -> it.copyTo(tmpFile.toFile(), true) }
      .let { (tmpDir, tmpFile) -> FileReference(tmpDir, tmpFile) }
  } ?: downloadRemoteFile(url.toURL(), userID)
