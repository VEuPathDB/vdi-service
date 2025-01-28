package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import vdi.component.db.cache.withTransaction

internal fun updateDatasetMeta(userID: UserID, datasetID: DatasetID, patch: DatasetPatchRequest) {
  val cacheDB = vdi.component.db.cache.CacheDB()

  val dataset = cacheDB.selectDataset(datasetID)
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

  cacheDB.withTransaction {
    val newMeta = VDIDatasetMeta(
      type             = meta.type,
      projects         = meta.projects,
      visibility       = patch.visibility?.toInternalVisibility() ?: meta.visibility,
      owner            = userID,
      name             = patch.name ?: meta.name,
      shortName        = if (patch.shortName?.isBlank() == true) null else patch.shortName ?: meta.shortName,
      shortAttribution = if (patch.shortAttribution?.isBlank() == true) null else patch.shortAttribution ?: meta.shortAttribution,
      category         = if (patch.category?.isBlank() == true) null else patch.category ?: meta.category,
      summary          = if (patch.summary?.isBlank() == true) null else patch.summary ?: meta.summary,
      description      = if (patch.description?.isBlank() == true) null else patch.description ?: meta.description,
      origin           = meta.origin,
      dependencies     = meta.dependencies,
      sourceURL        = meta.sourceURL,
      created          = meta.created,
      publications     = if (patch.publications?.isEmpty() == true)
        emptyList()
      else
        patch.publications?.map(DatasetPublication::toInternal) ?: meta.publications,
      hyperlinks       = if (patch.hyperlinks?.isEmpty() == true)
        emptyList()
      else
        patch.hyperlinks?.map(DatasetHyperlink::toInternal) ?: meta.hyperlinks,
      contacts         = if (patch.contacts?.isEmpty() == true)
        emptyList()
      else
        patch.contacts?.map(DatasetContact::toInternal) ?: meta.contacts,
      taxonIDs         = if (patch.taxonIds?.isEmpty() == true)
        emptyList()
      else
        patch.taxonIds ?: meta.taxonIDs,
    )

    DatasetStore.putDatasetMeta(userID, datasetID, newMeta)
    it.updateDatasetMeta(datasetID, newMeta)
  }
}

private fun DatasetPatchRequest.hasSomethingToUpdate(): Boolean =
  name != null
  || summary != null
  || description != null
  || visibility != null
  || shortName != null
  || shortAttribution != null
  || category != null
  || publications != null
  || hyperlinks != null
  || contacts != null
  ||taxonIds != null
