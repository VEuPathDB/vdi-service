package org.veupathdb.service.vdi.generated.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
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

@Path("/vdi-datasets/{vd-id}")
public interface VdiDatasetsVdId {
  @GET
  @Produces("application/json")
  GetVdiDatasetsByVdIdResponse getVdiDatasetsByVdId(@PathParam("vd-id") String vdId);

  @PATCH
  @Produces("application/json")
  @Consumes("application/json")
  PatchVdiDatasetsByVdIdResponse patchVdiDatasetsByVdId(@PathParam("vd-id") String vdId,
      DatasetPatchRequest entity);

  @DELETE
  @Produces("application/json")
  DeleteVdiDatasetsByVdIdResponse deleteVdiDatasetsByVdId(@PathParam("vd-id") String vdId);

  class GetVdiDatasetsByVdIdResponse extends ResponseDelegate {
    private GetVdiDatasetsByVdIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsByVdIdResponse(Response response) {
      super(response);
    }

    public static GetVdiDatasetsByVdIdResponse respond200WithApplicationJson(
        DatasetDetails entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsByVdIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsByVdIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsByVdIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsByVdIdResponse respond404WithApplicationJson(NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsByVdIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsByVdIdResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsByVdIdResponse(responseBuilder.build(), entity);
    }
  }

  class DeleteVdiDatasetsByVdIdResponse extends ResponseDelegate {
    private DeleteVdiDatasetsByVdIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private DeleteVdiDatasetsByVdIdResponse(Response response) {
      super(response);
    }

    public static DeleteVdiDatasetsByVdIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new DeleteVdiDatasetsByVdIdResponse(responseBuilder.build());
    }

    public static DeleteVdiDatasetsByVdIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteVdiDatasetsByVdIdResponse(responseBuilder.build(), entity);
    }

    public static DeleteVdiDatasetsByVdIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteVdiDatasetsByVdIdResponse(responseBuilder.build(), entity);
    }

    public static DeleteVdiDatasetsByVdIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteVdiDatasetsByVdIdResponse(responseBuilder.build(), entity);
    }

    public static DeleteVdiDatasetsByVdIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new DeleteVdiDatasetsByVdIdResponse(responseBuilder.build(), entity);
    }
  }

  class PatchVdiDatasetsByVdIdResponse extends ResponseDelegate {
    private PatchVdiDatasetsByVdIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PatchVdiDatasetsByVdIdResponse(Response response) {
      super(response);
    }

    public static PatchVdiDatasetsByVdIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PatchVdiDatasetsByVdIdResponse(responseBuilder.build());
    }

    public static PatchVdiDatasetsByVdIdResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdIdResponse(responseBuilder.build(), entity);
    }

    public static PatchVdiDatasetsByVdIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdIdResponse(responseBuilder.build(), entity);
    }

    public static PatchVdiDatasetsByVdIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdIdResponse(responseBuilder.build(), entity);
    }

    public static PatchVdiDatasetsByVdIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdIdResponse(responseBuilder.build(), entity);
    }

    public static PatchVdiDatasetsByVdIdResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdIdResponse(responseBuilder.build(), entity);
    }

    public static PatchVdiDatasetsByVdIdResponse respond425() {
      Response.ResponseBuilder responseBuilder = Response.status(425);
      return new PatchVdiDatasetsByVdIdResponse(responseBuilder.build());
    }

    public static PatchVdiDatasetsByVdIdResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchVdiDatasetsByVdIdResponse(responseBuilder.build(), entity);
    }
  }
}
