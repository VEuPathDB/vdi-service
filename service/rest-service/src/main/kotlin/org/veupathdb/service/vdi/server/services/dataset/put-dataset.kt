package org.veupathdb.service.vdi.server.services.dataset

import org.veupathdb.service.vdi.generated.model.DatasetPatchRequestBodyImpl
import org.veupathdb.service.vdi.generated.model.DatasetPutRequestBody
import org.veupathdb.service.vdi.generated.resources.DatasetsVdiId.PutDatasetsByVdiIdResponse
import org.veupathdb.service.vdi.generated.resources.DatasetsVdiId.PutDatasetsByVdiIdResponse.*
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.service.vdi.server.inputs.cleanup
import org.veupathdb.service.vdi.server.inputs.validate
import org.veupathdb.service.vdi.server.outputs.ForbiddenError
import org.veupathdb.service.vdi.server.outputs.NotFoundError
import org.veupathdb.service.vdi.server.outputs.UnprocessableEntityError
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.cache.CacheDB

internal fun putDataset(
  userID:    UserID,
  datasetID: DatasetID,
  request:   DatasetPutRequestBody,
): PutDatasetsByVdiIdResponse {
  val originalDataset = CacheDB().selectDatasetForUser(userID, datasetID)
    ?: return respond404WithApplicationJson(NotFoundError())

  if (originalDataset.isDeleted)
    return respond403WithApplicationJson(ForbiddenError("cannot add revisions to a deleted dataset"))

  request.cleanup()
  request.validate(originalDataset.projects)
    .takeUnless { it.isEmpty }
    ?.let { respond422WithApplicationJson(UnprocessableEntityError(it)) }

  // validate type change
  val targetType = (request as DatasetPatchRequestBodyImpl).optValidateType {
    return respond403WithApplicationJson(ForbiddenError("cannot change the type of ${it.displayName} datasets"))
  }

  val meta = DatasetStore.getDatasetMeta(userID, datasetID)
    ?: throw IllegalStateException("target dataset has no $DatasetMetaFilename file")

  val newDatasetID = DatasetID()

  // add .{inc} to dataset id to create new id
  // submit new dataset (a la create-dataset)
  // on successful submission:
  //   create revision flag in original dataset directory
  //   mark original dataset as deleted in cache db
  // return redirect to new dataset id
}

private fun DatasetID.incrementRevision(): DatasetID {

}
