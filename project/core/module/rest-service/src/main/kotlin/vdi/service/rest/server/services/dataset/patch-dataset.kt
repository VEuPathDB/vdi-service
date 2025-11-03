@file:JvmName("DatasetPatchService")
package vdi.service.rest.server.services.dataset

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.convertValue
import org.veupathdb.lib.request.validation.ValidationErrors
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.withTransaction
import vdi.core.install.InstallTargetRegistry
import vdi.json.JSON
import vdi.model.data.DatasetID
import vdi.model.data.DatasetMetadata
import vdi.model.data.UserID
import vdi.service.rest.generated.model.DatasetPatchRequestBody
import vdi.service.rest.generated.resources.DatasetsVdiId.PatchDatasetsByVdiIdResponse
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.inputs.applyPatch
import vdi.service.rest.server.inputs.cleanup
import vdi.service.rest.server.inputs.validate
import vdi.service.rest.server.outputs.ForbiddenError
import vdi.service.rest.server.outputs.Static404
import vdi.service.rest.server.outputs.UnprocessableEntityError
import vdi.service.rest.server.outputs.wrap
import vdi.service.rest.util.Either
import vdi.service.rest.util.Either.Companion.left
import vdi.service.rest.util.Either.Companion.right
import vdi.service.rest.util.leftOrNull

fun <T: ControllerBase> T.updateDatasetMeta(datasetID: DatasetID, patch: DatasetPatchRequestBody): PatchDatasetsByVdiIdResponse {
  val cacheDB = CacheDB()

  val dataset = cacheDB.selectDataset(datasetID)
    ?: return Static404.wrap()

  if (dataset.ownerID != userID)
    return Static404.wrap()

  if (dataset.isDeleted)
    return ForbiddenError("cannot update a deleted dataset").wrap()

  if (!patch.hasSomethingToUpdate())
    return PatchDatasetsByVdiIdResponse.respond204()

  val patchedMetadata = patch.validateAndApply(userID, datasetID).run {
    leftOrNull()?.also { return UnprocessableEntityError(it).wrap() }
    unwrapRight()
  }

  cacheDB.withTransaction { db ->
    DatasetStore.putDatasetMeta(userID, datasetID, patchedMetadata)
    db.updateDatasetMeta(datasetID, patchedMetadata)
  }

  return PatchDatasetsByVdiIdResponse.respond204()
}

private fun DatasetPatchRequestBody.validateAndApply(
  userID: UserID,
  datasetID: DatasetID,
): Either<ValidationErrors, DatasetMetadata> {
  val originalMetadata = DatasetStore.getDatasetMeta(userID, datasetID)!!

  cleanup()

  // Run standard validation.
  val errors = validate(originalMetadata)

  if (errors.isNotEmpty)
    return left(errors)

  // Apply patch to get new metadata
  val newMetadata = applyPatch(originalMetadata)

  // Optionally apply project-specific JSON schema validation
  val validators = originalMetadata.installTargets
    .mapNotNull { InstallTargetRegistry[it]!!.metaValidation }

  if (validators.isNotEmpty()) {
    val serialized = JSON.convertValue<ObjectNode>(newMetadata)
    for (schema in validators) {
      serialized.validate(schema, errors)
    }
  }

  if (errors.isNotEmpty)
    return left(errors)

  return right(newMetadata)
}

private fun DatasetPatchRequestBody.hasSomethingToUpdate(): Boolean =
  type != null
  || visibility != null
  || name != null
  || summary != null
  || description != null
  || publications != null
  || contacts != null
  || projectName != null
  || programName != null
  || linkedDatasets != null
  || characteristics != null
  || externalIdentifiers != null
  || funding != null
