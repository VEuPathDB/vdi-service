package vdi.service.server.services.dataset

import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.withTransaction
import vdi.service.generated.model.DatasetPatchRequestBody
import vdi.service.generated.resources.DatasetsVdiId.PatchDatasetsByVdiIdResponse
import vdi.service.s3.DatasetStore
import vdi.service.server.inputs.cleanup
import vdi.service.server.inputs.validate
import vdi.service.server.outputs.ForbiddenError
import vdi.service.server.outputs.Static404
import vdi.service.server.outputs.UnprocessableEntityError
import vdi.service.server.outputs.wrap

fun updateDatasetMeta(userID: UserID, datasetID: DatasetID, patch: DatasetPatchRequestBody): PatchDatasetsByVdiIdResponse {
  val cacheDB = CacheDB()

  val dataset = cacheDB.selectDataset(datasetID)
    ?: Static404.wrap()

  if (dataset.isDeleted)
    return ForbiddenError("cannot update metadata on a deleted dataset").wrap()

  if (dataset.ownerID != userID)
    return ForbiddenError("cannot update metadata on a dataset you do not own").wrap()

  if (!patch.hasSomethingToUpdate())
    return PatchDatasetsByVdiIdResponse.respond204()

  patch.cleanup()
  patch.validate(dataset.projects)
    .takeUnless { it.isEmpty }
    ?.also { return UnprocessableEntityError(it).wrap() }

  // validate type change
  val targetType = patch.optValidateType {
    return ForbiddenError("cannot change the type of ${it.displayName} datasets").wrap()
  }

  val meta = DatasetStore.getDatasetMeta(userID, datasetID)
    ?: throw IllegalStateException("target dataset has no $DatasetMetaFilename file")

  cacheDB.withTransaction { db ->
    meta.applyPatch(userID, targetType, patch).also {
      DatasetStore.putDatasetMeta(userID, datasetID, it)
      db.updateDatasetMeta(datasetID, it)
    }
  }

  return PatchDatasetsByVdiIdResponse.respond204()
}

private fun DatasetPatchRequestBody.hasSomethingToUpdate(): Boolean =
  // POJO field null tests have been ordered based on the order of fields as
  // they appear in the API docs.  Not important, but helpful for verifying that
  // all fields are covered.
  name != null
  || datasetType != null
  || shortName != null
  || shortAttribution != null
  || category != null
  || visibility != null
  || summary != null
  || description != null
  || publications != null
  || hyperlinks != null
  || organisms != null
  || contacts != null
