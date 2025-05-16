package vdi.service.rest.generated.resources;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import vdi.service.rest.generated.model.AdminReportsImportsFailedGetOrder;
import vdi.service.rest.generated.model.AdminReportsImportsFailedGetSort;
import vdi.service.rest.generated.model.AllDatasetsListResponse;
import vdi.service.rest.generated.model.BadRequestError;
import vdi.service.rest.generated.model.BrokenDatasetInstallReportBody;
import vdi.service.rest.generated.model.BrokenImportListing;
import vdi.service.rest.generated.model.InternalDatasetDetails;
import vdi.service.rest.generated.model.NotFoundError;
import vdi.service.rest.generated.model.ServerError;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/admin/reports")
public interface AdminReports {
  String ROOT_PATH = "/admin/reports";

  String IMPORTS_FAILED_PATH = ROOT_PATH + "/imports/failed";

  String OBJECT_STORE_LIST_ALL_PATH = ROOT_PATH + "/object-store/list-all";

  String DATASETS_VDI_ID_PATH = ROOT_PATH + "/datasets/{vdi-id}";

  String DATASETS_LIST_ALL_PATH = ROOT_PATH + "/datasets/list-all";

  String INSTALLS_FAILED_PATH = ROOT_PATH + "/installs/failed";

  String VDI_ID_VAR = "{vdi-id}";

  @GET
  @Path("/datasets/list-all")
  @Produces("application/json")
  GetAdminReportsDatasetsListAllResponse getAdminReportsDatasetsListAll(
      @QueryParam("project_id") String projectId,
      @QueryParam("include_deleted") @DefaultValue("false") Boolean includeDeleted,
      @QueryParam("limit") @DefaultValue("100") Integer limit,
      @QueryParam("offset") @DefaultValue("0") Integer offset);

  @GET
  @Path("/datasets/{vdi-id}")
  @Produces("application/json")
  GetAdminReportsDatasetsByVdiIdResponse getAdminReportsDatasetsByVdiId(
      @PathParam("vdi-id") String vdiId);

  @GET
  @Path("/imports/failed")
  @Produces("application/json")
  GetAdminReportsImportsFailedResponse getAdminReportsImportsFailed(@QueryParam("user") Long user,
      @QueryParam("before") String before, @QueryParam("after") String after,
      @QueryParam("sort") @DefaultValue("date") AdminReportsImportsFailedGetSort sort,
      @QueryParam("order") @DefaultValue("desc") AdminReportsImportsFailedGetOrder order,
      @QueryParam("limit") @DefaultValue("100") Integer limit,
      @QueryParam("offset") @DefaultValue("0") Integer offset);

  @GET
  @Path("/installs/failed")
  @Produces("application/json")
  GetAdminReportsInstallsFailedResponse getAdminReportsInstallsFailed(
      @QueryParam("expanded") @DefaultValue("true") Boolean expanded);

  @GET
  @Path("/object-store/list-all")
  @Produces({
      "application/json",
      "text/plain"
  })
  GetAdminReportsObjectStoreListAllResponse getAdminReportsObjectStoreListAll();

  class GetAdminReportsDatasetsListAllResponse extends ResponseDelegate {
    public GetAdminReportsDatasetsListAllResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetAdminReportsDatasetsListAllResponse(Response response) {
      super(response);
    }

    public GetAdminReportsDatasetsListAllResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetAdminReportsDatasetsListAllResponse respond200WithApplicationJson(
        AllDatasetsListResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminReportsDatasetsListAllResponse(responseBuilder.build(), entity);
    }

    public static GetAdminReportsDatasetsListAllResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminReportsDatasetsListAllResponse(responseBuilder.build(), entity);
    }
  }

  class GetAdminReportsDatasetsByVdiIdResponse extends ResponseDelegate {
    public GetAdminReportsDatasetsByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetAdminReportsDatasetsByVdiIdResponse(Response response) {
      super(response);
    }

    public GetAdminReportsDatasetsByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetAdminReportsDatasetsByVdiIdResponse respond200WithApplicationJson(
        InternalDatasetDetails entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminReportsDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetAdminReportsDatasetsByVdiIdResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminReportsDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetAdminReportsDatasetsByVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminReportsDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetAdminReportsDatasetsByVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminReportsDatasetsByVdiIdResponse(responseBuilder.build(), entity);
    }
  }

  class GetAdminReportsImportsFailedResponse extends ResponseDelegate {
    public GetAdminReportsImportsFailedResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetAdminReportsImportsFailedResponse(Response response) {
      super(response);
    }

    public GetAdminReportsImportsFailedResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetAdminReportsImportsFailedResponse respond200WithApplicationJson(
        BrokenImportListing entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminReportsImportsFailedResponse(responseBuilder.build(), entity);
    }

    public static GetAdminReportsImportsFailedResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminReportsImportsFailedResponse(responseBuilder.build(), entity);
    }

    public static GetAdminReportsImportsFailedResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminReportsImportsFailedResponse(responseBuilder.build(), entity);
    }
  }

  class GetAdminReportsInstallsFailedResponse extends ResponseDelegate {
    public GetAdminReportsInstallsFailedResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetAdminReportsInstallsFailedResponse(Response response) {
      super(response);
    }

    public GetAdminReportsInstallsFailedResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetAdminReportsInstallsFailedResponse respond200WithApplicationJson(
        BrokenDatasetInstallReportBody entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminReportsInstallsFailedResponse(responseBuilder.build(), entity);
    }

    public static GetAdminReportsInstallsFailedResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminReportsInstallsFailedResponse(responseBuilder.build(), entity);
    }
  }

  class GetAdminReportsObjectStoreListAllResponse extends ResponseDelegate {
    public GetAdminReportsObjectStoreListAllResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetAdminReportsObjectStoreListAllResponse(Response response) {
      super(response);
    }

    public GetAdminReportsObjectStoreListAllResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetAdminReportsObjectStoreListAllResponse respond200WithTextPlain(Object entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "text/plain");
      responseBuilder.entity(entity);
      return new GetAdminReportsObjectStoreListAllResponse(responseBuilder.build(), entity);
    }

    public static GetAdminReportsObjectStoreListAllResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminReportsObjectStoreListAllResponse(responseBuilder.build(), entity);
    }
  }
}
