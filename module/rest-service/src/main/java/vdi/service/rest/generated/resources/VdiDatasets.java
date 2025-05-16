package vdi.service.rest.generated.resources;

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
import vdi.service.rest.generated.model.BadRequestError;
import vdi.service.rest.generated.model.DatasetListEntry;
import vdi.service.rest.generated.model.DatasetPostRequestBody;
import vdi.service.rest.generated.model.DatasetPostResponseBody;
import vdi.service.rest.generated.model.FailedDependencyError;
import vdi.service.rest.generated.model.ServerError;
import vdi.service.rest.generated.model.UnauthorizedError;
import vdi.service.rest.generated.model.UnprocessableEntityError;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/vdi-datasets")
public interface VdiDatasets {
  String ROOT_PATH = "/vdi-datasets";

  @GET
  @Produces("application/json")
  GetVdiDatasetsResponse getVdiDatasets(@QueryParam("project_id") String projectId,
      @QueryParam("ownership") @DefaultValue("any") String ownership);

  @POST
  @Produces("application/json")
  @Consumes("multipart/form-data")
  PostVdiDatasetsResponse postVdiDatasets(DatasetPostRequestBody entity);

  class GetVdiDatasetsResponse extends ResponseDelegate {
    public GetVdiDatasetsResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetVdiDatasetsResponse(Response response) {
      super(response);
    }

    public GetVdiDatasetsResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
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
    public PostVdiDatasetsResponse(Response response, Object entity) {
      super(response, entity);
    }

    public PostVdiDatasetsResponse(Response response) {
      super(response);
    }

    public PostVdiDatasetsResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static HeadersFor201 headersFor201() {
      return new HeadersFor201();
    }

    public static PostVdiDatasetsResponse respond201WithApplicationJson(
        DatasetPostResponseBody entity, HeadersFor201 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(201).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
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

    public static PostVdiDatasetsResponse respond424WithApplicationJson(
        FailedDependencyError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(424).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsResponse(responseBuilder.build(), entity);
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
