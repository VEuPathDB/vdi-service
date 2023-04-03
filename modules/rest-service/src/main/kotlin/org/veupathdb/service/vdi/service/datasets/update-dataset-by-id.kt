package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.veupathdb.lib.container.jaxrs.errors.UnprocessableEntityException
import org.veupathdb.service.vdi.generated.model.DatasetPatchRequest
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.DatasetMetaImpl

fun updateDatasetMeta(userID: UserID, datasetID: DatasetID, patch: DatasetPatchRequest) {
  // Validate patch request
  if (patch.name?.isBlank() == true)
    throw UnprocessableEntityException(mapOf("name" to listOf("cannot be blank")))

  // Lookup dataset
  val dataset = CacheDB.selectDataset(datasetID) ?: throw NotFoundException()

  // ensure user owns dataset
  if (dataset.ownerID != userID)
    throw ForbiddenException()

  // apply metadata patch to dataset
  CacheDB.openTransaction().use {
    it.updateDatasetMeta(DatasetMetaImpl(
      datasetID,
      patch.name ?: dataset.name,
      patch.summary ?: dataset.summary,
      patch.description ?: dataset.description,
    ))
  }
}