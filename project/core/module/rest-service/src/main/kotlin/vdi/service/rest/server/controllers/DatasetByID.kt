package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.slf4j.Logger
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import vdi.model.data.DatasetID
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.DatasetPatchRequestBody
import vdi.service.rest.generated.model.DatasetPutRequestBody
import vdi.service.rest.generated.resources.DatasetsVdiId
import vdi.service.rest.generated.resources.DatasetsVdiId.*
import vdi.service.rest.generated.resources.DatasetsVdiIdFiles
import vdi.service.rest.server.outputs.BadRequestError
import vdi.service.rest.server.outputs.wrap
import vdi.service.rest.server.services.dataset.*

@Authenticated
class DatasetByID(@Context request: ContainerRequest, @Context val uploadConfig: UploadConfig)
  : DatasetsVdiId
  , ControllerBase(request)
{
  @Authenticated(adminOverride = ALLOW_ALWAYS)
  override fun getDatasetsByVdiId(rawID: String): GetDatasetsByVdiIdResponse =
    DatasetID(rawID).let { vdiID ->
      when (maybeUserID) {
        null -> adminGetDatasetByID(vdiID)
        else -> userGetDatasetByID(vdiID)
      }.let {
        if (it.isLeft) {
          it.unwrapLeft()
            .apply {
              revisionHistory
                ?.revisions
                ?.forEach { e -> e.fileListUrl = createURL(DatasetsVdiIdFiles.ROOT_PATH.replace(VDI_ID_VAR, e.revisionId)) } }
            .let(GetDatasetsByVdiIdResponse::respond200WithApplicationJson)
        } else {
          // If the dataset could not be found under the given dataset ID, then
          // check if it has been revised.  If there is a newer revision,
          // redirect to the new dataset ID endpoint.
          it.unwrapRight().let { oRes ->
            if (oRes.status == 404)
              getLatestRevision(vdiID, ::redirectURL) ?: oRes
            else
              oRes
          }
        }
      }
    }

  override fun patchDatasetsByVdiId(vdiID: String, entity: DatasetPatchRequestBody?) =
    entity?.let { body -> updateDatasetMeta(DatasetID(vdiID), body) }
      ?: BadRequestError("request body cannot be empty").wrap()

  override fun putDatasetsByVdiId(vdiId: String, entity: DatasetPutRequestBody?) =
    entity
      ?.let { body -> putDataset(DatasetID(vdiId), body, uploadConfig) }
      ?.let {
        if (it.isLeft)
          it.unwrapLeft()
            .let { body -> PutDatasetsByVdiIdResponse.respond202WithApplicationJson(
              body,
              PutDatasetsByVdiIdResponse.headersFor202()
                .withLocation(ROOT_PATH.replace(VDI_ID_VAR, body.datasetId))
            ) }
        else
          it.unwrapRight()
      }
      ?: BadRequestError("empty request body").wrap()

  @Authenticated(adminOverride = ALLOW_ALWAYS)
  override fun deleteDatasetsByVdiId(vdiID: String) =
    when (maybeUser) {
      null -> adminDeleteDataset(DatasetID(vdiID))
      else -> userDeleteDataset(DatasetID(vdiID))
    }
}
