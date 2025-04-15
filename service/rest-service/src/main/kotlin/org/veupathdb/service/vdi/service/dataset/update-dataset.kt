@file:Suppress("NOTHING_TO_INLINE")
package org.veupathdb.service.vdi.service.dataset

import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.service.vdi.generated.resources.DatasetsVdiId.PatchDatasetsByVdiIdResponse
import org.veupathdb.service.vdi.generated.resources.DatasetsVdiId.PatchDatasetsByVdiIdResponse.*
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.service.vdi.server.inputs.cleanup
import org.veupathdb.service.vdi.server.inputs.toInternal
import org.veupathdb.service.vdi.server.inputs.validate
import org.veupathdb.service.vdi.server.outputs.ForbiddenError
import org.veupathdb.service.vdi.server.outputs.NotFoundError
import org.veupathdb.service.vdi.server.outputs.UnprocessableEntityError
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.withTransaction
import vdi.lib.plugin.registry.PluginRegistry

internal fun updateDatasetMeta(userID: UserID, datasetID: DatasetID, patch: DatasetPatchRequestBody): PatchDatasetsByVdiIdResponse {
  val cacheDB = CacheDB()

  val dataset = cacheDB.selectDataset(datasetID)
    ?: return respond404WithApplicationJson(NotFoundError())

  if (dataset.isDeleted)
    return respond403WithApplicationJson(ForbiddenError("cannot update metadata on a deleted dataset"))

  if (dataset.ownerID != userID)
    return respond403WithApplicationJson(ForbiddenError("cannot update metadata on a dataset you do not own"))

  if (!patch.hasSomethingToUpdate())
    return respond204()

  patch.cleanup()
  patch.validate(dataset.projects).also {
    if (!it.isEmpty)
      return respond422WithApplicationJson(UnprocessableEntityError(it))
  }

  var targetType: VDIDatasetType? = null

  // validate type change
  if (patch.datasetType != null) {
    if (!PluginRegistry[dataset.typeName, dataset.typeVersion]!!.changesEnabled)
      return respond403WithApplicationJson(ForbiddenError("cannot update the type of datasets of type (${dataset.typeName}, ${dataset.typeVersion})"))

    targetType = patch.datasetType.toInternal()
  }

  val meta = DatasetStore.getDatasetMeta(userID, datasetID)
    ?: throw IllegalStateException("target dataset has no $DatasetMetaFilename file")

  cacheDB.withTransaction {
    val newMeta = VDIDatasetMeta(
      type             = targetType ?: meta.type,
      projects         = meta.projects,
      visibility       = patch.visibility?.toInternal() ?: meta.visibility,
      owner            = userID,
      name             = patch.name ?: meta.name,
      shortName        = patch.shortName.applyPatch(meta.shortName),
      shortAttribution = patch.shortAttribution.applyPatch(meta.shortAttribution),
      category         = patch.category.applyPatch(meta.category),
      summary          = patch.summary.applyPatch(meta.summary),
      description      = patch.description.applyPatch(meta.description),
      origin           = meta.origin,
      dependencies     = meta.dependencies,
      sourceURL        = meta.sourceURL,
      created          = meta.created,
      publications     = patch.publications.applyPatch(meta.publications, DatasetPublication::toInternal),
      hyperlinks       = patch.hyperlinks.applyPatch(meta.hyperlinks, DatasetHyperlink::toInternal),
      contacts         = patch.contacts.applyPatch(meta.contacts, DatasetContact::toInternal),
      organisms        = patch.organisms.applyPatch(meta.organisms),
    )

    DatasetStore.putDatasetMeta(userID, datasetID, newMeta)
    it.updateDatasetMeta(datasetID, newMeta)
  }

  return respond204()
}

private fun DatasetPatchRequestBody.hasSomethingToUpdate(): Boolean =
  // POJO field null tests have been ordered based on the order of fields as
  // they appear in the API docs.
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

/**
 * Returns the update value for a string field based on the following rules:
 *
 * 1. If the input string is null, nothing to update, use the original value.
 * 2. If the input string is blank, set the field to null.
 * 3. If the input string has content, use the input string.
 */
private inline fun String?.applyPatch(original: String?): String? =
  when {
    // null input == don't update
    this == null -> original
    // blank input == clear value
    isEmpty() -> null
    // non-null && non-empty == update value
    else -> this
  }

/**
 * Returns the update value for a collection field based on the following rules:
 *
 * 1. If the input is null, use the original value.
 * 2. If the input is empty, clear the field.
 * 3. If the input has content, use the input.
 */
private inline fun <T> Collection<T>?.applyPatch(original: Collection<T>): Collection<T> =
  when {
    // null input == don't update
    this == null -> original
    // empty input == clear value
    isEmpty() -> emptyList()
    // non-null && non-empty == update value
    else -> this
  }

/**
 * Returns the update value for a collection field based on the following rules:
 *
 * 1. If the input is null, use the original value.
 * 2. If the input is empty, clear the field.
 * 3. If the input has content, use the input.
 */
private inline fun <E, I> Collection<E>?.applyPatch(original: Collection<I>, translate: (E) -> I): Collection<I> =
  when {
    // null input == don't update
    this == null -> original
    // empty input == clear value
    isEmpty() -> emptyList()
    // non-null && non-empty == update value
    else -> map(translate)
  }
