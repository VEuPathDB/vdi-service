package org.veupathdb.service.vdi.generated.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.model.BadRequestError;
import org.veupathdb.service.vdi.generated.model.DatasetDetails;
import org.veupathdb.service.vdi.generated.model.DatasetPatchRequest;
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
      @PathParam("vdi-id") String vdiId, DatasetPatchRequest entity);

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

    public static PatchDatasetsByVdIdAndVdiIdResponse respond425() {
      Response.ResponseBuilder responseBuilder = Response.status(425);
      return new PatchDatasetsByVdIdAndVdiIdResponse(responseBuilder.build());
    }

    public static PatchDatasetsByVdIdAndVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchDatasetsByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }
  }
}
