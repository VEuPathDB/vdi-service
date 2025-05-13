@file:JvmName("DatasetPutService")
package vdi.service.rest.server.services.dataset

import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevision
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevisionAction
import java.time.OffsetDateTime
import java.time.ZoneOffset
import vdi.lib.db.app.AppDB
import vdi.lib.db.app.model.InstallStatus
import vdi.lib.db.app.model.InstallType
import vdi.lib.db.cache.CacheDB
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.DatasetPatchRequestBody
import vdi.service.rest.generated.model.DatasetPutRequestBody
import vdi.service.rest.generated.model.DatasetPutResponseBody
import vdi.service.rest.generated.model.DatasetPutResponseBodyImpl
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

// TODO: don't allow users to revise a dataset that has another revision already in progress!

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
  val targetType = (request.meta as DatasetPatchRequestBody).optValidateType {
    return Either.ofRight(ForbiddenError("cannot change the type of ${it.displayName} datasets").wrap())
  }

  val newestRevisionID = CacheDB().selectLatestRevision(datasetID)?.revisionID ?: datasetID

  val originalMeta = DatasetStore.getDatasetMeta(userID, datasetID)
    ?: throw IllegalStateException("target dataset has no $DatasetMetaFilename file")

  // If a newer revision of the dataset already exists, only allow revising from
  // this point if the newer one failed irrecoverably.
  if (newestRevisionID != datasetID && isRevisionBlocker(newestRevisionID, originalMeta))
    return Either.ofRight(ForbiddenError("cannot revise a dataset with another revision already in progress").wrap())

  // add .{inc} to dataset id to create new id
  var newDatasetID = (CacheDB().selectLatestRevision(datasetID)?.revisionID ?: datasetID)
    .incrementRevision()

  while (DatasetStore.getImportReadyZipSize(userID, newDatasetID) > -1L) {
    newDatasetID = newDatasetID.incrementRevision()
  }

  val newMeta = originalMeta.let { it.applyPatch(
    userID          = userID,
    targetType      = targetType,
    patch           = request.meta,
    originalID      = it.originalID ?: datasetID,
    revisionHistory = it.revisionHistory + VDIDatasetRevision(
      action       = VDIDatasetRevisionAction.Revise,
      timestamp    = OffsetDateTime.now(ZoneOffset.UTC),
      revisionID   = newDatasetID,
      revisionNote = request.meta.revisionNote,
    ),
  ) }

  val (tempDirectory, uploadFile) = CacheDB().initializeDataset(userID, newDatasetID, newMeta) {
    it.tryInsertRevisionLink(newMeta.originalID ?: datasetID, newMeta.revisionHistory.last())
    resolveDatasetFile(request.file, request.url, uploadConfig)
  }

  submitUpload(newDatasetID, tempDirectory, uploadFile, newMeta, uploadConfig)

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

private fun isRevisionBlocker(datasetID: DatasetID, meta: VDIDatasetMeta) =
  meta.projects.any { target ->
    AppDB().accessor(target, meta.type.name)!!
      .selectDatasetInstallMessages(datasetID)
      .associate { it.installType to it.status }
      .let { when {
        // first try to check the data-install status
        it[InstallType.Data].let { s -> when (s) {
          // dataset has no data install status, don't block
          null                             -> false
          // newer revision is currently installing
          InstallStatus.Running            -> true
          // newer revision completed while this request was processing
          InstallStatus.Complete           -> true
          // unrecoverable user error, this dataset cannot be used and should not block revisions
          InstallStatus.FailedValidation   -> false
          // newer revision failed for bug and needs admin input
          InstallStatus.FailedInstallation -> true
          // unrecoverable user error, this dataset cannot be used and should not block revisions
          InstallStatus.MissingDependency  -> false
          // newer revision failed but will be retried
          InstallStatus.ReadyForReinstall  -> true
        } } -> true

        // no install-data record exists, check install-meta next
        it[InstallType.Meta].let { s -> when (s) {
          // dataset has no meta install status, don't block
          null                             -> false
          // newer revision is currently installing
          InstallStatus.Running            -> true
          // newer revision meta complete, install will be attempted shortly
          InstallStatus.Complete           -> true
          // unrecoverable user error, this dataset cannot be used and should not block revisions
          InstallStatus.FailedValidation   -> false
          // newer revision failed for bug and needs admin input
          InstallStatus.FailedInstallation -> true
          // unrecoverable user error, this dataset cannot be used and should not block revisions
          InstallStatus.MissingDependency  -> false
          // newer revision failed but will be retried
          InstallStatus.ReadyForReinstall  -> true
        } } -> true

        // no blocking data or meta install
        else -> false
      } }
  }
