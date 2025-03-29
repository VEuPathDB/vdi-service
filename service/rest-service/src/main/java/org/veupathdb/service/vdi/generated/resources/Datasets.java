package org.veupathdb.service.vdi.generated.resources;

import java.util.List;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.model.BadRequestError;
import org.veupathdb.service.vdi.generated.model.DatasetListEntry;
import org.veupathdb.service.vdi.generated.model.DatasetPostRequest;
import org.veupathdb.service.vdi.generated.model.DatasetPostResponse;
import org.veupathdb.service.vdi.generated.model.FailedDependencyError;
import org.veupathdb.service.vdi.generated.model.ServerError;
import org.veupathdb.service.vdi.generated.model.UnauthorizedError;
import org.veupathdb.service.vdi.generated.model.UnprocessableEntityError;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

@Path("/datasets")
public interface Datasets {
  @GET
  @Produces("application/json")
  GetDatasetsResponse getDatasets(@QueryParam("project_id") String projectId,
      @QueryParam("ownership") @DefaultValue("any") String ownership);

  @POST
  @Produces("application/json")
  @Consumes("multipart/form-data")
  PostDatasetsResponse postDatasets(DatasetPostRequest entity);

  class GetDatasetsResponse extends ResponseDelegate {
    private GetDatasetsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetDatasetsResponse(Response response) {
      super(response);
    }

    public static GetDatasetsResponse respond200WithApplicationJson(List<DatasetListEntry> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<DatasetListEntry>> wrappedEntity = new GenericEntity<List<DatasetListEntry>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetDatasetsResponse(responseBuilder.build(), wrappedEntity);
    }

    public static GetDatasetsResponse respond400WithApplicationJson(BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsResponse respond401WithApplicationJson(UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsResponse(responseBuilder.build(), entity);
    }
  }

  class PostDatasetsResponse extends ResponseDelegate {
    private PostDatasetsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostDatasetsResponse(Response response) {
      super(response);
    }

    public static PostDatasetsResponse respond200WithApplicationJson(DatasetPostResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostDatasetsResponse(responseBuilder.build(), entity);
    }

    public static PostDatasetsResponse respond400WithApplicationJson(BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostDatasetsResponse(responseBuilder.build(), entity);
    }

    public static PostDatasetsResponse respond401WithApplicationJson(UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostDatasetsResponse(responseBuilder.build(), entity);
    }

    public static PostDatasetsResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostDatasetsResponse(responseBuilder.build(), entity);
    }

    public static PostDatasetsResponse respond424WithApplicationJson(FailedDependencyError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(424).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostDatasetsResponse(responseBuilder.build(), entity);
    }

    public static PostDatasetsResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostDatasetsResponse(responseBuilder.build(), entity);
    }
  }
}
