package org.veupathdb.service.vdi.generated.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.model.BadRequestError;
import org.veupathdb.service.vdi.generated.model.BrokenDatasetListing;
import org.veupathdb.service.vdi.generated.model.DatasetPostRequest;
import org.veupathdb.service.vdi.generated.model.DatasetPostResponse;
import org.veupathdb.service.vdi.generated.model.ForbiddenError;
import org.veupathdb.service.vdi.generated.model.InstallCleanupRequest;
import org.veupathdb.service.vdi.generated.model.ServerError;
import org.veupathdb.service.vdi.generated.model.UnauthorizedError;
import org.veupathdb.service.vdi.generated.model.UnprocessableEntityError;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

@Path("/vdi-datasets/admin")
public interface VdiDatasetsAdmin {
  @POST
  @Path("/proxy-upload")
  @Produces("application/json")
  @Consumes("multipart/form-data")
  PostVdiDatasetsAdminProxyUploadResponse postVdiDatasetsAdminProxyUpload(
      @HeaderParam("Admin-Token") String adminToken, @HeaderParam("User-ID") Long userID,
      DatasetPostRequest entity);

  @GET
  @Path("/failed-imports")
  GetVdiDatasetsAdminFailedImportsResponse getVdiDatasetsAdminFailedImports(
      @HeaderParam("Admin-Token") String adminToken);

  @GET
  @Path("/list-broken")
  @Produces("application/json")
  GetVdiDatasetsAdminListBrokenResponse getVdiDatasetsAdminListBroken(
      @QueryParam("expanded") @DefaultValue("true") Boolean expanded,
      @HeaderParam("Admin-Token") String adminToken);

  @POST
  @Path("/install-cleanup")
  @Produces("application/json")
  @Consumes("application/json")
  PostVdiDatasetsAdminInstallCleanupResponse postVdiDatasetsAdminInstallCleanup(
      @HeaderParam("Admin-Token") String adminToken, InstallCleanupRequest entity);

  @POST
  @Path("/delete-cleanup")
  @Produces("application/json")
  PostVdiDatasetsAdminDeleteCleanupResponse postVdiDatasetsAdminDeleteCleanup(
      @HeaderParam("Admin-Token") String adminToken);

  class PostVdiDatasetsAdminProxyUploadResponse extends ResponseDelegate {
    private PostVdiDatasetsAdminProxyUploadResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostVdiDatasetsAdminProxyUploadResponse(Response response) {
      super(response);
    }

    public static PostVdiDatasetsAdminProxyUploadResponse respond200WithApplicationJson(
        DatasetPostResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminProxyUploadResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminProxyUploadResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminProxyUploadResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminProxyUploadResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminProxyUploadResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminProxyUploadResponse(responseBuilder.build(), entity);
    }
  }

  class GetVdiDatasetsAdminFailedImportsResponse extends ResponseDelegate {
    private GetVdiDatasetsAdminFailedImportsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsAdminFailedImportsResponse(Response response) {
      super(response);
    }

    public static GetVdiDatasetsAdminFailedImportsResponse respond200() {
      Response.ResponseBuilder responseBuilder = Response.status(200);
      return new GetVdiDatasetsAdminFailedImportsResponse(responseBuilder.build());
    }
  }

  class GetVdiDatasetsAdminListBrokenResponse extends ResponseDelegate {
    private GetVdiDatasetsAdminListBrokenResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsAdminListBrokenResponse(Response response) {
      super(response);
    }

    public static GetVdiDatasetsAdminListBrokenResponse respond200WithApplicationJson(
        BrokenDatasetListing entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsAdminListBrokenResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsAdminListBrokenResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsAdminListBrokenResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsAdminListBrokenResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsAdminListBrokenResponse(responseBuilder.build(), entity);
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
