package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.veupathdb.lib.container.jaxrs.errors.UnprocessableEntityException
import org.veupathdb.service.vdi.generated.model.DatasetPatchRequest
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.cache.OldCacheDB
import vdi.component.db.cache.model.DatasetMeta
import vdi.component.db.cache.model.DatasetRecord

fun updateDatasetMeta(userID: UserID, datasetID: DatasetID, patch: DatasetPatchRequest) {
  // Validate patch request
  if (patch.name?.isBlank() == true)
    throw UnprocessableEntityException(mapOf("name" to listOf("cannot be blank")))

  // Lookup dataset
  val dataset: DatasetRecord = OldCacheDB.selectDataset(datasetID) ?: throw NotFoundException()

  // ensure user owns dataset
  if (dataset.ownerID != userID)
    throw ForbiddenException()

  // apply metadata patch to dataset
  OldCacheDB.openTransaction().use {
    it.updateDatasetMeta(object : DatasetMeta {
      override val datasetID   get() = datasetID
      override val name        get() = patch.name ?: dataset.name
      override val summary     get() = patch.summary ?: dataset.summary
      override val description get() = patch.description ?: dataset.description
    })
  }
}