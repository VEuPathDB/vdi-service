@file:JvmName("DatasetPutService")
package vdi.service.rest.server.services.dataset

import java.time.OffsetDateTime
import vdi.core.db.app.AppDB
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.core.db.cache.CacheDB
import vdi.model.DatasetMetaFilename
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.DatasetRevision
import vdi.model.meta.DatasetRevisionHistory
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.DatasetPutRequestBody
import vdi.service.rest.generated.model.DatasetPutResponseBody
import vdi.service.rest.generated.model.DatasetPutResponseBodyImpl
import vdi.service.rest.generated.resources.DatasetsVdiId.PutDatasetsByVdiIdResponse
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.inputs.applyPatch
import vdi.service.rest.server.inputs.cleanup
import vdi.service.rest.server.inputs.validate
import vdi.service.rest.server.outputs.ForbiddenError
import vdi.service.rest.server.outputs.Static404
import vdi.service.rest.server.outputs.UnprocessableEntityError
import vdi.service.rest.server.outputs.wrap
import vdi.util.fn.Either

// TODO: don't allow users to revise a dataset that has another revision already in progress!

internal fun ControllerBase.putDataset(
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

  val originalMeta = DatasetStore.getDatasetMeta(userID, datasetID)
    ?: throw IllegalStateException("target dataset has no $DatasetMetaFilename file")

  request.cleanup()
  request.validate(originalMeta)
    .takeUnless { it.isEmpty }
    ?.let { return Either.ofRight(UnprocessableEntityError(it).wrap()) }

  val newestRevisionID = CacheDB().selectLatestRevision(datasetID)?.revisionID ?: datasetID

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

  val newHistoryEntry = DatasetRevision(
    DatasetRevision.Action.Revise,
    OffsetDateTime.now(),
    newDatasetID,
    request.details.revisionNote,
  )

  val newHistory = originalMeta.revisionHistory
    ?.let { DatasetRevisionHistory(it.originalID, it.revisions + newHistoryEntry) }
    ?: DatasetRevisionHistory(datasetID, listOf(
      DatasetRevision(DatasetRevision.Action.Create, OffsetDateTime.now(), datasetID, "dataset initial version"),
      newHistoryEntry,
    ))

  val newMeta = request.details.applyPatch(originalMeta, newHistory)

  request.dataFile
    ?.let { verifyFileExtensions(it, newMeta.type) }
    ?.also { return Either.ofRight(it.wrap()) }

  val uploadRefs = CacheDB().initializeDataset(newDatasetID, newMeta) {
    it.tryInsertRevisionLink(newHistory.originalID, newHistory.revisions.last())
    resolveDatasetFiles(request.dataFile, request.url, request.docFile, emptyList(), uploadConfig)
  }

  submitUpload(newDatasetID, uploadRefs, newMeta, uploadConfig)

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
      ?.let { DatasetID(raw.take(it) + (raw.substring(it).toInt() + 1)) }
      ?: DatasetID("$raw.1")
  }

private fun isRevisionBlocker(datasetID: DatasetID, meta: DatasetMetadata) =
  meta.installTargets.any { target ->
    AppDB().accessor(target, meta.type)!!
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
