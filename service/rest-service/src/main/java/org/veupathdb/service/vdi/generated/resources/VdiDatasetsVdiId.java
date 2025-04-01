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

@Path("/vdi-datasets/{vdi-id}")
public interface VdiDatasetsVdiId {
  @GET
  @Produces("application/json")
  GetVdiDatasetsByVdIdAndVdiIdResponse getVdiDatasetsByVdIdAndVdiId(@PathParam("vd-id") String vdId,
      @PathParam("vdi-id") String vdiId);

  @PATCH
  @Produces("application/json")
  @Consumes("application/json")
  PatchVdiDatasetsByVdIdAndVdiIdResponse patchVdiDatasetsByVdIdAndVdiId(
      @PathParam("vd-id") String vdId, @PathParam("vdi-id") String vdiId,
      DatasetPatchRequestBody entity);

  @PUT
  @Produces("application/json")
  @Consumes("application/json")
  PutVdiDatasetsByVdIdAndVdiIdResponse putVdiDatasetsByVdIdAndVdiId(@PathParam("vd-id") String vdId,
      @PathParam("vdi-id") String vdiId, DatasetPutRequestBody entity);

  @DELETE
  @Produces("application/json")
  DeleteVdiDatasetsByVdIdAndVdiIdResponse deleteVdiDatasetsByVdIdAndVdiId(
      @PathParam("vd-id") String vdId, @PathParam("vdi-id") String vdiId);

  class PutVdiDatasetsByVdIdAndVdiIdResponse extends ResponseDelegate {
    private PutVdiDatasetsByVdIdAndVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PutVdiDatasetsByVdIdAndVdiIdResponse(Response response) {
      super(response);
    }

    public static HeadersFor201 headersFor201() {
      return new HeadersFor201();
    }

    public static PutVdiDatasetsByVdIdAndVdiIdResponse respond201WithApplicationJson(
        DatasetPutResponseBody entity, HeadersFor201 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(201).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new PutVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsByVdIdAndVdiIdResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsByVdIdAndVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsByVdIdAndVdiIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsByVdIdAndVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsByVdIdAndVdiIdResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsByVdIdAndVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
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

  class GetVdiDatasetsByVdIdAndVdiIdResponse extends ResponseDelegate {
    private GetVdiDatasetsByVdIdAndVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsByVdIdAndVdiIdResponse(Response response) {
      super(response);
    }

    public static GetVdiDatasetsByVdIdAndVdiIdResponse respond200WithApplicationJson(
        DatasetDetails entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static HeadersFor301 headersFor301() {
      return new HeadersFor301();
    }

    public static GetVdiDatasetsByVdIdAndVdiIdResponse respond301(HeadersFor301 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(301);
      responseBuilder = headers.toResponseBuilder(responseBuilder);
      return new GetVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build());
    }

    public static GetVdiDatasetsByVdIdAndVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsByVdIdAndVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsByVdIdAndVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
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

  class PatchVdiDatasetsByVdIdAndVdiIdResponse extends ResponseDelegate {
    private PatchVdiDatasetsByVdIdAndVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PatchVdiDatasetsByVdIdAndVdiIdResponse(Response response) {
      super(response);
    }

    public static PatchVdiDatasetsByVdIdAndVdiIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PatchVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build());
    }

    public static PatchVdiDatasetsByVdIdAndVdiIdResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchVdiDatasetsByVdIdAndVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchVdiDatasetsByVdIdAndVdiIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchVdiDatasetsByVdIdAndVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchVdiDatasetsByVdIdAndVdiIdResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static PatchVdiDatasetsByVdIdAndVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }
  }

  class DeleteVdiDatasetsByVdIdAndVdiIdResponse extends ResponseDelegate {
    private DeleteVdiDatasetsByVdIdAndVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private DeleteVdiDatasetsByVdIdAndVdiIdResponse(Response response) {
      super(response);
    }

    public static DeleteVdiDatasetsByVdIdAndVdiIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new DeleteVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build());
    }

    public static DeleteVdiDatasetsByVdIdAndVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static DeleteVdiDatasetsByVdIdAndVdiIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static DeleteVdiDatasetsByVdIdAndVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static DeleteVdiDatasetsByVdIdAndVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteVdiDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }
  }
}
