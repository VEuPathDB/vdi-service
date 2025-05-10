package vdi.service.rest.generated.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import vdi.service.rest.generated.model.*;
import vdi.service.rest.generated.support.PATCH;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/vdi-datasets/{vdi-id}")
public interface VdiDatasetsVdiId {
  String ROOT_PATH = "/vdi-datasets/{vdi-id}";

  String VDI_ID_VAR = "{vdi-id}";

  @GET
  @Produces("application/json")
  GetVdiDatasetsByVdiIdResponse getVdiDatasetsByVdiId(@PathParam("vdi-id") String vdiId);

  @PATCH
  @Produces("application/json")
  @Consumes("application/json")
  PatchVdiDatasetsByVdiIdResponse patchVdiDatasetsByVdiId(@PathParam("vdi-id") String vdiId,
      DatasetPatchRequestBody entity);

  @PUT
  @Produces("application/json")
  @Consumes("application/json")
  PutVdiDatasetsByVdiIdResponse putVdiDatasetsByVdiId(@PathParam("vdi-id") String vdiId,
      DatasetPutRequestBody entity);

  @DELETE
  @Produces("application/json")
  DeleteVdiDatasetsByVdiIdResponse deleteVdiDatasetsByVdiId(@PathParam("vdi-id") String vdiId);

  class PutVdiDatasetsByVdiIdResponse extends ResponseDelegate {
    public PutVdiDatasetsByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public PutVdiDatasetsByVdiIdResponse(Response response) {
      super(response);
    }

    public PutVdiDatasetsByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static HeadersFor201 headersFor201() {
      return new HeadersFor201();
    }

    public static PutVdiDatasetsByVdiIdResponse respond201WithApplicationJson(
        DatasetPutResponseBody entity, HeadersFor201 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(201).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new PutVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsByVdiIdResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsByVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsByVdiIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsByVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsByVdiIdResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsByVdiIdResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
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

  class GetVdiDatasetsByVdiIdResponse extends ResponseDelegate {
    public GetVdiDatasetsByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetVdiDatasetsByVdiIdResponse(Response response) {
      super(response);
    }

    public GetVdiDatasetsByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetVdiDatasetsByVdiIdResponse respond200WithApplicationJson(
        DatasetDetails entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static HeadersFor301 headersFor301() {
      return new HeadersFor301();
    }

    public static GetVdiDatasetsByVdiIdResponse respond301(HeadersFor301 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(301);
      responseBuilder = headers.toResponseBuilder(responseBuilder);
      return new GetVdiDatasetsByVdiIdResponse(responseBuilder.build());
    }

    public static GetVdiDatasetsByVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsByVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsByVdiIdResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
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

  class PatchVdiDatasetsByVdiIdResponse extends ResponseDelegate {
    public PatchVdiDatasetsByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public PatchVdiDatasetsByVdiIdResponse(Response response) {
      super(response);
    }

    public PatchVdiDatasetsByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static PatchVdiDatasetsByVdiIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PatchVdiDatasetsByVdiIdResponse(responseBuilder.build());
    }

    public static PatchVdiDatasetsByVdiIdResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchVdiDatasetsByVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchVdiDatasetsByVdiIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchVdiDatasetsByVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchVdiDatasetsByVdiIdResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchVdiDatasetsByVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }
  }

  class DeleteVdiDatasetsByVdiIdResponse extends ResponseDelegate {
    public DeleteVdiDatasetsByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public DeleteVdiDatasetsByVdiIdResponse(Response response) {
      super(response);
    }

    public DeleteVdiDatasetsByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static DeleteVdiDatasetsByVdiIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new DeleteVdiDatasetsByVdiIdResponse(responseBuilder.build());
    }

    public static DeleteVdiDatasetsByVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static DeleteVdiDatasetsByVdiIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static DeleteVdiDatasetsByVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static DeleteVdiDatasetsByVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteVdiDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }
  }
}
