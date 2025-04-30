package vdi.service.rest.generated.resources;

import java.util.List;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import vdi.service.rest.generated.model.DatasetListEntry;
import vdi.service.rest.generated.model.ServerError;
import vdi.service.rest.generated.model.UnauthorizedError;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/vdi-datasets/community")
public interface VdiDatasetsCommunity {
  String ROOT_PATH = "/vdi-datasets/community";

  @GET
  @Produces("application/json")
  GetVdiDatasetsCommunityResponse getVdiDatasetsCommunity();

  class GetVdiDatasetsCommunityResponse extends ResponseDelegate {
    public GetVdiDatasetsCommunityResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetVdiDatasetsCommunityResponse(Response response) {
      super(response);
    }

    public GetVdiDatasetsCommunityResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetVdiDatasetsCommunityResponse respond200WithApplicationJson(
        List<DatasetListEntry> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<DatasetListEntry>> wrappedEntity = new GenericEntity<List<DatasetListEntry>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetVdiDatasetsCommunityResponse(responseBuilder.build(), wrappedEntity);
    }

    public static GetVdiDatasetsCommunityResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsCommunityResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsCommunityResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsCommunityResponse(responseBuilder.build(), entity);
    }
  }
}
