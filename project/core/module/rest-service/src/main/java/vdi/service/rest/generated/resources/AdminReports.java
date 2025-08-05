package vdi.service.rest.generated.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
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
      @QueryParam("install_target") String installTarget,
      @QueryParam("include_deleted") @DefaultValue("false") boolean includeDeleted,
      @QueryParam("limit") @DefaultValue("100") int limit,
      @QueryParam("offset") @DefaultValue("0") int offset);

  @GET
  @Path("/datasets/{vdi-id}")
  @Produces("application/json")
  GetAdminReportsDatasetsByVdiIdResponse getAdminReportsDatasetsByVdiId(
      @PathParam("vdi-id") String vdiId);

  @GET
  @Path("/imports/failed")
  @Produces("application/json")
  GetAdminReportsImportsFailedResponse getAdminReportsImportsFailed(@QueryParam("user") long user,
      @QueryParam("before") String before, @QueryParam("after") String after,
      @QueryParam("sort") @DefaultValue("date") AdminReportsImportsFailedGetSort sort,
      @QueryParam("order") @DefaultValue("desc") AdminReportsImportsFailedGetOrder order,
      @QueryParam("limit") @DefaultValue("100") int limit,
      @QueryParam("offset") @DefaultValue("0") int offset);

  @GET
  @Path("/installs/failed")
  @Produces("application/json")
  GetAdminReportsInstallsFailedResponse getAdminReportsInstallsFailed(
      @QueryParam("expanded") @DefaultValue("true") boolean expanded);

  @GET
  @Path("/object-store/list-all")
  @Produces({
      "application/json",
      "text/plain"
  })
  GetAdminReportsObjectStoreListAllResponse getAdminReportsObjectStoreListAll();

  class GetAdminReportsDatasetsListAllResponse extends ResponseDelegate {
    private GetAdminReportsDatasetsListAllResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetAdminReportsDatasetsListAllResponse(Response response) {
      super(response);
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
    private GetAdminReportsDatasetsByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetAdminReportsDatasetsByVdiIdResponse(Response response) {
      super(response);
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
    private GetAdminReportsImportsFailedResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetAdminReportsImportsFailedResponse(Response response) {
      super(response);
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
    private GetAdminReportsInstallsFailedResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetAdminReportsInstallsFailedResponse(Response response) {
      super(response);
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
    private GetAdminReportsObjectStoreListAllResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetAdminReportsObjectStoreListAllResponse(Response response) {
      super(response);
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
