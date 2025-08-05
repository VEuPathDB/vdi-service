package vdi.service.rest.generated.resources;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import vdi.service.rest.generated.model.BadRequestError;
import vdi.service.rest.generated.model.DatasetListEntry;
import vdi.service.rest.generated.model.DatasetPostRequestBody;
import vdi.service.rest.generated.model.DatasetPostResponseBody;
import vdi.service.rest.generated.model.FailedDependencyError;
import vdi.service.rest.generated.model.ServerError;
import vdi.service.rest.generated.model.UnauthorizedError;
import vdi.service.rest.generated.model.UnprocessableEntityError;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/datasets")
public interface Datasets {
  String ROOT_PATH = "/datasets";

  @GET
  @Produces("application/json")
  GetDatasetsResponse getDatasets(@QueryParam("install_target") String installTarget,
      @QueryParam("ownership") @DefaultValue("any") String ownership);

  @POST
  @Produces("application/json")
  @Consumes("multipart/form-data")
  PostDatasetsResponse postDatasets(DatasetPostRequestBody entity);

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
