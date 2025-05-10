package vdi.service.rest.generated.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import vdi.service.rest.generated.model.*;
import vdi.service.rest.generated.support.ResponseDelegate;

import java.util.List;

@Path("/datasets")
public interface Datasets {
  String ROOT_PATH = "/datasets";

  @GET
  @Produces("application/json")
  GetDatasetsResponse getDatasets(@QueryParam("project_id") String projectId,
      @QueryParam("ownership") @DefaultValue("any") String ownership);

  @POST
  @Produces("application/json")
  @Consumes("multipart/form-data")
  PostDatasetsResponse postDatasets(DatasetPostRequestBody entity);

  class GetDatasetsResponse extends ResponseDelegate {
    public GetDatasetsResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetDatasetsResponse(Response response) {
      super(response);
    }

    public GetDatasetsResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
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
    public PostDatasetsResponse(Response response, Object entity) {
      super(response, entity);
    }

    public PostDatasetsResponse(Response response) {
      super(response);
    }

    public PostDatasetsResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static HeadersFor201 headersFor201() {
      return new HeadersFor201();
    }

    public static PostDatasetsResponse respond201WithApplicationJson(DatasetPostResponseBody entity,
        HeadersFor201 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(201).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
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

    public static class HeadersFor201 extends HeaderBuilderBase {
      private HeadersFor201() {
      }

      public HeadersFor201 withLocation(final String p) {
        headerMap.put("Location", String.valueOf(p));;
        return this;
      }
    }
  }
}
