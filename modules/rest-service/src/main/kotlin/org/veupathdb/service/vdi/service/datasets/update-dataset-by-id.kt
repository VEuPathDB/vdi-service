package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.veupathdb.service.vdi.generated.model.DatasetPatchRequest
import org.veupathdb.service.vdi.generated.model.toInternalVisibility
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.service.vdi.util.ValidationErrors
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMetaImpl
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.DatasetMetaImpl

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
    ?: throw IllegalStateException("target dataset has no $DatasetMetaFilename file")

  CacheDB.withTransaction {
    val visibility = patch.visibility?.toInternalVisibility() ?: meta.visibility
    val name = patch.name ?: meta.name
    val summary = if (patch.summary?.isBlank() == true) null else patch.summary ?: meta.summary
    val description = if (patch.description?.isBlank() == true) null else patch.description ?: meta.description

    DatasetStore.putDatasetMeta(
      userID,
      datasetID,
      VDIDatasetMetaImpl(
        type         = meta.type,
        projects     = meta.projects,
        visibility   = visibility,
        owner        = userID,
        name         = name,
        summary      = summary,
        description  = description,
        origin       = meta.origin,
        dependencies = meta.dependencies,
        sourceURL    = meta.sourceURL,
      )
    )
    it.updateDatasetMeta(DatasetMetaImpl(datasetID, visibility, name, summary, description, meta.sourceURL))
  }
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