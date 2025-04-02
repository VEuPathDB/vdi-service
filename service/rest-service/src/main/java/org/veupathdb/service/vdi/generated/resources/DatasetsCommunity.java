package org.veupathdb.service.vdi.generated.resources;

import java.util.List;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.model.DatasetListEntry;
import org.veupathdb.service.vdi.generated.model.ServerError;
import org.veupathdb.service.vdi.generated.model.UnauthorizedError;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

@Path("/datasets/community")
public interface DatasetsCommunity {
  @GET
  @Produces("application/json")
  GetDatasetsCommunityResponse getDatasetsCommunity();

  class GetDatasetsCommunityResponse extends ResponseDelegate {
    public GetDatasetsCommunityResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetDatasetsCommunityResponse(Response response) {
      super(response);
    }

    public GetDatasetsCommunityResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetDatasetsCommunityResponse respond200WithApplicationJson(
        List<DatasetListEntry> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<DatasetListEntry>> wrappedEntity = new GenericEntity<List<DatasetListEntry>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetDatasetsCommunityResponse(responseBuilder.build(), wrappedEntity);
    }

    public static GetDatasetsCommunityResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsCommunityResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsCommunityResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsCommunityResponse(responseBuilder.build(), entity);
    }
  }
}
