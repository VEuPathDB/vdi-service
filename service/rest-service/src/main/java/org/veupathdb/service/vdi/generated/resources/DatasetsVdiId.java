package org.veupathdb.service.vdi.generated.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.model.BadRequestError;
import org.veupathdb.service.vdi.generated.model.DatasetDetails;
import org.veupathdb.service.vdi.generated.model.DatasetPatchRequestBody;
import org.veupathdb.service.vdi.generated.model.DatasetPutRequestBody;
import org.veupathdb.service.vdi.generated.model.DatasetPutResponseBody;
import org.veupathdb.service.vdi.generated.model.ForbiddenError;
import org.veupathdb.service.vdi.generated.model.NotFoundError;
import org.veupathdb.service.vdi.generated.model.ServerError;
import org.veupathdb.service.vdi.generated.model.UnauthorizedError;
import org.veupathdb.service.vdi.generated.model.UnprocessableEntityError;
import org.veupathdb.service.vdi.generated.support.PATCH;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

@Path("/datasets/{vdi-id}")
public interface DatasetsVdiId {
  @GET
  @Produces("application/json")
  GetDatasetsByVdIdAndVdiIdResponse getDatasetsByVdIdAndVdiId(@PathParam("vd-id") String vdId,
      @PathParam("vdi-id") String vdiId);

  @PATCH
  @Produces("application/json")
  @Consumes("application/json")
  PatchDatasetsByVdIdAndVdiIdResponse patchDatasetsByVdIdAndVdiId(@PathParam("vd-id") String vdId,
      @PathParam("vdi-id") String vdiId, DatasetPatchRequestBody entity);

  @PUT
  @Produces("application/json")
  @Consumes("application/json")
  PutDatasetsByVdIdAndVdiIdResponse putDatasetsByVdIdAndVdiId(@PathParam("vd-id") String vdId,
      @PathParam("vdi-id") String vdiId, DatasetPutRequestBody entity);

  @DELETE
  @Produces("application/json")
  DeleteDatasetsByVdIdAndVdiIdResponse deleteDatasetsByVdIdAndVdiId(@PathParam("vd-id") String vdId,
      @PathParam("vdi-id") String vdiId);

  class GetDatasetsByVdIdAndVdiIdResponse extends ResponseDelegate {
    private GetDatasetsByVdIdAndVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetDatasetsByVdIdAndVdiIdResponse(Response response) {
      super(response);
    }

    public static GetDatasetsByVdIdAndVdiIdResponse respond200WithApplicationJson(
        DatasetDetails entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static HeadersFor301 headersFor301() {
      return new HeadersFor301();
    }

    public static GetDatasetsByVdIdAndVdiIdResponse respond301(HeadersFor301 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(301);
      responseBuilder = headers.toResponseBuilder(responseBuilder);
      return new GetDatasetsByVdIdAndVdiIdResponse(responseBuilder.build());
    }

    public static GetDatasetsByVdIdAndVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsByVdIdAndVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsByVdIdAndVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
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

  class DeleteDatasetsByVdIdAndVdiIdResponse extends ResponseDelegate {
    private DeleteDatasetsByVdIdAndVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private DeleteDatasetsByVdIdAndVdiIdResponse(Response response) {
      super(response);
    }

    public static DeleteDatasetsByVdIdAndVdiIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new DeleteDatasetsByVdIdAndVdiIdResponse(responseBuilder.build());
    }

    public static DeleteDatasetsByVdIdAndVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static DeleteDatasetsByVdIdAndVdiIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static DeleteDatasetsByVdIdAndVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static DeleteDatasetsByVdIdAndVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }
  }

  class PutDatasetsByVdIdAndVdiIdResponse extends ResponseDelegate {
    private PutDatasetsByVdIdAndVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PutDatasetsByVdIdAndVdiIdResponse(Response response) {
      super(response);
    }

    public static HeadersFor201 headersFor201() {
      return new HeadersFor201();
    }

    public static PutDatasetsByVdIdAndVdiIdResponse respond201WithApplicationJson(
        DatasetPutResponseBody entity, HeadersFor201 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(201).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new PutDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsByVdIdAndVdiIdResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsByVdIdAndVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsByVdIdAndVdiIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsByVdIdAndVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsByVdIdAndVdiIdResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsByVdIdAndVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
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

  class PatchDatasetsByVdIdAndVdiIdResponse extends ResponseDelegate {
    private PatchDatasetsByVdIdAndVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PatchDatasetsByVdIdAndVdiIdResponse(Response response) {
      super(response);
    }

    public static PatchDatasetsByVdIdAndVdiIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PatchDatasetsByVdIdAndVdiIdResponse(responseBuilder.build());
    }

    public static PatchDatasetsByVdIdAndVdiIdResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchDatasetsByVdIdAndVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchDatasetsByVdIdAndVdiIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchDatasetsByVdIdAndVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchDatasetsByVdIdAndVdiIdResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchDatasetsByVdIdAndVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }
  }
}
