package vdi.service.rest.server.services.dataset

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.convertValue
import org.veupathdb.lib.request.validation.ValidationErrors
import vdi.core.db.app.AppDB
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.withTransaction
import vdi.core.install.InstallTargetRegistry
import vdi.core.plugin.registry.PluginRegistry
import vdi.json.JSON
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.DatasetVisibility
import vdi.service.rest.generated.model.DatasetPatchRequestBody
import vdi.service.rest.generated.model.JsonField
import vdi.service.rest.generated.resources.DatasetsVdiId.PatchDatasetsByVdiIdResponse
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.inputs.applyPatch
import vdi.service.rest.server.inputs.cleanup
import vdi.service.rest.server.inputs.hasSomethingToUpdate
import vdi.service.rest.server.inputs.validate
import vdi.service.rest.server.outputs.*
import vdi.util.fn.Either
import vdi.util.fn.Either.Companion.left
import vdi.util.fn.Either.Companion.right
import vdi.util.fn.leftOrNull

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

  val originalMetadata = DatasetStore.getDatasetMeta(userID, datasetID)
    ?: return TooEarlyError("dataset is not yet ready to be updated").wrap()

  val patchedMetadata = patch.validateAndApply(datasetID, originalMetadata)
    .run {
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
  datasetID: DatasetID,
  original:  DatasetMetadata,
): Either<ValidationErrors, DatasetMetadata> {
  cleanup()

  // Run standard validation.
  val errors = validate(original)

  if (errors.isNotEmpty)
    return left(errors)

  // Apply patch to get new metadata
  val newMetadata = applyPatch(original)

  // Optionally apply project-specific JSON schema validation
  val validators = original.installTargets
    .mapNotNull { InstallTargetRegistry[it]!!.metaValidation }

  if (validators.isNotEmpty()) {
    val serialized = JSON.convertValue<ObjectNode>(newMetadata)
    for (schema in validators) {
      serialized.validate(schema, errors)
    }
  }

  if (errors.isNotEmpty)
    return left(errors)

  // If the PATCH body seems valid at a surface level, move on to performing
  // heavier validations.

  if (newMetadata.isPromotingToCommunity(original))
    newMetadata.validateForPromotion(datasetID, errors)

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
  || (studyCharacteristics != null && studyCharacteristics.hasSomethingToUpdate())
  || (externalIdentifiers != null && externalIdentifiers.hasSomethingToUpdate())
  || funding != null

/**
 * Tests if the PATCH request is attempting to promote the target dataset to a
 * community dataset.
 */
private fun DatasetMetadata.isPromotingToCommunity(original: DatasetMetadata) =
  visibility != DatasetVisibility.Private && original.visibility == DatasetVisibility.Private

private fun DatasetMetadata.validateForPromotion(datasetID: DatasetID, errors: ValidationErrors) {
  val typeConfig = PluginRegistry.require(type)

  // If the target data type doesn't use variable properties files, then there
  // is no extra validation the VDI core service can perform.  All additional
  // validation is done by the type-specific plugin.
  if (!typeConfig.usesDataPropertiesFiles)
    return

  val metaInstallsSucceeded = installTargets
    .asSequence()
    .map { AppDB(it, type)?.selectDatasetInstallMessages(datasetID) }
    .flatMap { it?.asSequence() ?: sequenceOf(null) }
    .all { it?.status == InstallStatus.Complete }

  if (!metaInstallsSucceeded)
    errors.add(JsonField.VISIBILITY, "cannot promote dataset to community with dataset install failures")
}