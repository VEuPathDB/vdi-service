@file:JvmName("DatasetPatchService")
package vdi.service.rest.server.services.dataset

import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.withTransaction
import vdi.model.DatasetMetaFilename
import vdi.model.data.DatasetID
import vdi.service.rest.generated.model.DatasetPatchRequestBody
import vdi.service.rest.generated.resources.DatasetsVdiId.PatchDatasetsByVdiIdResponse
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.inputs.cleanup
import vdi.service.rest.server.inputs.validate
import vdi.service.rest.server.outputs.ForbiddenError
import vdi.service.rest.server.outputs.Static404
import vdi.service.rest.server.outputs.UnprocessableEntityError
import vdi.service.rest.server.outputs.wrap

fun <T: ControllerBase> T.updateDatasetMeta(datasetID: DatasetID, patch: DatasetPatchRequestBody): PatchDatasetsByVdiIdResponse {
  val cacheDB = CacheDB()

  val dataset = cacheDB.selectDataset(datasetID)
    ?: return Static404.wrap()

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
  || visibility != null
  || summary != null
  || description != null
  || publications != null
  || hyperlinks != null
  || organisms != null
  || contacts != null
  || properties != null
