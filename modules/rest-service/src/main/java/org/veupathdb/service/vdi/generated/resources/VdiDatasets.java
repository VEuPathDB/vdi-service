package org.veupathdb.service.vdi.generated.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.model.*;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

import java.util.List;

@Path("/vdi-datasets")
public interface VdiDatasets {
  @GET
  @Produces("application/json")
  GetVdiDatasetsResponse getVdiDatasets(@QueryParam("project_id") String projectId,
      @QueryParam("ownership") @DefaultValue("any") String ownership);

  @POST
  @Produces("application/json")
  @Consumes("multipart/form-data")
  PostVdiDatasetsResponse postVdiDatasets(DatasetPostRequest entity);

  class GetVdiDatasetsResponse extends ResponseDelegate {
    private GetVdiDatasetsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsResponse(Response response) {
      super(response);
    }

    public static GetVdiDatasetsResponse respond200WithApplicationJson(
        List<DatasetListEntry> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<DatasetListEntry>> wrappedEntity = new GenericEntity<List<DatasetListEntry>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetVdiDatasetsResponse(responseBuilder.build(), wrappedEntity);
    }

    public static GetVdiDatasetsResponse respond400WithApplicationJson(BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsResponse respond401WithApplicationJson(UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsResponse(responseBuilder.build(), entity);
    }
  }

  class PostVdiDatasetsResponse extends ResponseDelegate {
    private PostVdiDatasetsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostVdiDatasetsResponse(Response response) {
      super(response);
    }

    public static PostVdiDatasetsResponse respond200WithApplicationJson(
        DatasetPostResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsResponse respond400WithApplicationJson(BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsResponse respond401WithApplicationJson(UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsResponse(responseBuilder.build(), entity);
    }
  }
}
