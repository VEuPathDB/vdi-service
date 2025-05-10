package vdi.service.rest.generated.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import vdi.service.rest.generated.model.*;
import vdi.service.rest.generated.support.PATCH;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/datasets/{vdi-id}")
public interface DatasetsVdiId {
  String ROOT_PATH = "/datasets/{vdi-id}";

  String VDI_ID_VAR = "{vdi-id}";

  @GET
  @Produces("application/json")
  GetDatasetsByVdiIdResponse getDatasetsByVdiId(@PathParam("vdi-id") String vdiId);

  @PATCH
  @Produces("application/json")
  @Consumes("application/json")
  PatchDatasetsByVdiIdResponse patchDatasetsByVdiId(@PathParam("vdi-id") String vdiId,
      DatasetPatchRequestBody entity);

  @PUT
  @Produces("application/json")
  @Consumes("application/json")
  PutDatasetsByVdiIdResponse putDatasetsByVdiId(@PathParam("vdi-id") String vdiId,
      DatasetPutRequestBody entity);

  @DELETE
  @Produces("application/json")
  DeleteDatasetsByVdiIdResponse deleteDatasetsByVdiId(@PathParam("vdi-id") String vdiId);

  class GetDatasetsByVdiIdResponse extends ResponseDelegate {
    public GetDatasetsByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetDatasetsByVdiIdResponse(Response response) {
      super(response);
    }

    public GetDatasetsByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetDatasetsByVdiIdResponse respond200WithApplicationJson(DatasetDetails entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static HeadersFor301 headersFor301() {
      return new HeadersFor301();
    }

    public static GetDatasetsByVdiIdResponse respond301(HeadersFor301 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(301);
      responseBuilder = headers.toResponseBuilder(responseBuilder);
      return new GetDatasetsByVdiIdResponse(responseBuilder.build());
    }

    public static GetDatasetsByVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsByVdiIdResponse respond404WithApplicationJson(NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsByVdiIdResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static class HeadersFor301 extends HeaderBuilderBase {
      private HeadersFor301() {
      }

      public HeadersFor301 withLocation(final String p) {
        headerMap.put("Location", String.valueOf(p));;
        return this;
      }
    }
  }

  class DeleteDatasetsByVdiIdResponse extends ResponseDelegate {
    public DeleteDatasetsByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public DeleteDatasetsByVdiIdResponse(Response response) {
      super(response);
    }

    public DeleteDatasetsByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static DeleteDatasetsByVdiIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new DeleteDatasetsByVdiIdResponse(responseBuilder.build());
    }

    public static DeleteDatasetsByVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static DeleteDatasetsByVdiIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static DeleteDatasetsByVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static DeleteDatasetsByVdiIdResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }
  }

  class PutDatasetsByVdiIdResponse extends ResponseDelegate {
    public PutDatasetsByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public PutDatasetsByVdiIdResponse(Response response) {
      super(response);
    }

    public PutDatasetsByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static HeadersFor201 headersFor201() {
      return new HeadersFor201();
    }

    public static PutDatasetsByVdiIdResponse respond201WithApplicationJson(
        DatasetPutResponseBody entity, HeadersFor201 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(201).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new PutDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsByVdiIdResponse respond400WithApplicationJson(BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsByVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsByVdiIdResponse respond403WithApplicationJson(ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsByVdiIdResponse respond404WithApplicationJson(NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsByVdiIdResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsByVdiIdResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsByVdiIdResponse(responseBuilder.build(), entity);
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

  class PatchDatasetsByVdiIdResponse extends ResponseDelegate {
    public PatchDatasetsByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public PatchDatasetsByVdiIdResponse(Response response) {
      super(response);
    }

    public PatchDatasetsByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static PatchDatasetsByVdiIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PatchDatasetsByVdiIdResponse(responseBuilder.build());
    }

    public static PatchDatasetsByVdiIdResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchDatasetsByVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchDatasetsByVdiIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchDatasetsByVdiIdResponse respond404WithApplicationJson(NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchDatasetsByVdiIdResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchDatasetsByVdiIdResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }
  }
}
