package org.veupathdb.service.vdi.generated.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.model.BadRequestError;
import org.veupathdb.service.vdi.generated.model.DeleteCleanupRequest;
import org.veupathdb.service.vdi.generated.model.InstallCleanupRequest;
import org.veupathdb.service.vdi.generated.model.ReconciliationRequest;
import org.veupathdb.service.vdi.generated.model.ServerError;
import org.veupathdb.service.vdi.generated.model.UnauthorizedError;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

@Path("/vdi-datasets/admin")
public interface VdiDatasetsAdmin {
  @POST
  @Path("/reconcile")
  @Produces("application/json")
  @Consumes("application/json")
  PostVdiDatasetsAdminReconcileResponse postVdiDatasetsAdminReconcile(ReconciliationRequest entity);

  @POST
  @Path("/install-cleanup")
  @Produces("application/json")
  @Consumes("application/json")
  PostVdiDatasetsAdminInstallCleanupResponse postVdiDatasetsAdminInstallCleanup(
      InstallCleanupRequest entity);

  @POST
  @Path("/delete-cleanup")
  @Produces("application/json")
  @Consumes("application/json")
  PostVdiDatasetsAdminDeleteCleanupResponse postVdiDatasetsAdminDeleteCleanup(
      DeleteCleanupRequest entity);

  class PostVdiDatasetsAdminReconcileResponse extends ResponseDelegate {
    private PostVdiDatasetsAdminReconcileResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostVdiDatasetsAdminReconcileResponse(Response response) {
      super(response);
    }

    public static PostVdiDatasetsAdminReconcileResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PostVdiDatasetsAdminReconcileResponse(responseBuilder.build());
    }

    public static PostVdiDatasetsAdminReconcileResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminReconcileResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminReconcileResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminReconcileResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminReconcileResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminReconcileResponse(responseBuilder.build(), entity);
    }
  }

  class PostVdiDatasetsAdminInstallCleanupResponse extends ResponseDelegate {
    private PostVdiDatasetsAdminInstallCleanupResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostVdiDatasetsAdminInstallCleanupResponse(Response response) {
      super(response);
    }

    public static PostVdiDatasetsAdminInstallCleanupResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PostVdiDatasetsAdminInstallCleanupResponse(responseBuilder.build());
    }

    public static PostVdiDatasetsAdminInstallCleanupResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminInstallCleanupResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminInstallCleanupResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminInstallCleanupResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminInstallCleanupResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminInstallCleanupResponse(responseBuilder.build(), entity);
    }
  }

  class PostVdiDatasetsAdminDeleteCleanupResponse extends ResponseDelegate {
    private PostVdiDatasetsAdminDeleteCleanupResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostVdiDatasetsAdminDeleteCleanupResponse(Response response) {
      super(response);
    }

    public static PostVdiDatasetsAdminDeleteCleanupResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PostVdiDatasetsAdminDeleteCleanupResponse(responseBuilder.build());
    }

    public static PostVdiDatasetsAdminDeleteCleanupResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminDeleteCleanupResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminDeleteCleanupResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminDeleteCleanupResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminDeleteCleanupResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminDeleteCleanupResponse(responseBuilder.build(), entity);
    }
  }
}
