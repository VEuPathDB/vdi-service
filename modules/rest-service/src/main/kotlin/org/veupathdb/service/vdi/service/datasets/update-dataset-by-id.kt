package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.veupathdb.service.vdi.generated.model.DatasetPatchRequest
import org.veupathdb.service.vdi.generated.model.toInternalVisibility
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.service.vdi.util.ValidationErrors
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMetaImpl
import org.veupathdb.vdi.lib.db.cache.CacheDB

internal fun updateDatasetMeta(userID: UserID, datasetID: DatasetID, patch: DatasetPatchRequest) {
  val dataset = CacheDB.selectDataset(datasetID)
    ?: throw NotFoundException()

  if (dataset.isDeleted)
    throw ForbiddenException("cannot update metadata on a deleted dataset")

  if (dataset.ownerID != userID)
    throw ForbiddenException("cannot update metadata on a dataset you do not own")

  if (!patch.hasSomethingToUpdate())
    return

  patch.validate()

  val meta = DatasetStore.getDatasetMeta(userID, datasetID)
    ?: throw IllegalStateException("target dataset has no meta.json file")

  DatasetStore.putDatasetMeta(
    userID,
    datasetID,
    VDIDatasetMetaImpl(
      type         = meta.type,
      projects     = meta.projects,
      visibility   = patch.visibility?.toInternalVisibility() ?: meta.visibility,
      owner        = userID,
      name         = patch.name ?: meta.name,
      summary      = if (patch.summary?.isBlank() == true) null else patch.summary ?: meta.summary,
      description  = if (patch.description?.isBlank() == true) null else patch.description ?: meta.description,
      origin       = meta.origin,
      dependencies = meta.dependencies,
    )
  )
}

private fun DatasetPatchRequest.hasSomethingToUpdate(): Boolean =
  name != null
    || summary != null
    || description != null
    || visibility != null

private fun DatasetPatchRequest.validate() {
  val errors = ValidationErrors()

  if (name?.isBlank() == true)
    errors.add("name", "Dataset name cannot be blank.")

  errors.throwIfNotEmpty()
}