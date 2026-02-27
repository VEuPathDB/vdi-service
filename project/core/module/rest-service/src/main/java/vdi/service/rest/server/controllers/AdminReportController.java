package vdi.service.rest.server.controllers;

import jakarta.ws.rs.Consumes;
import vdi.service.rest.generated.model.AdminReportsImportsFailedGetOrder;
import vdi.service.rest.generated.model.AdminReportsImportsFailedGetSort;
import vdi.service.rest.generated.resources.AdminReports;
import vdi.service.rest.server.services.admin.report.*;

public class AdminReportController implements AdminReports {
  @Override
  public GetAdminReportsDatasetsListAllResponse getAdminReportsDatasetsListAll(
    String installTarget,
    Boolean includeDeleted,
    Integer limit,
    Integer offset
  ) {
    return AllDatasetsList.listAllDatasets(offset, limit, installTarget, includeDeleted);
  }

  @Override
  public GetAdminReportsDatasetsByVdiIdResponse getAdminReportsDatasetsByVdiId(String vdiId) {
    return DatasetDetailsAdmin.getAdminDatasetDetails(vdiId);
  }

  @Override
  @Consumes("application/json")
  public GetAdminReportsImportsFailedResponse getAdminReportsImportsFailed(
    Long user,
    String before,
    String after,
    AdminReportsImportsFailedGetSort sort,
    AdminReportsImportsFailedGetOrder order,
    Integer limit,
    Integer offset
  ) {
    return FailedImportList.generateFailedImportReport(user, before, after, sort, order, limit, offset);
  }

  @Override
  public GetAdminReportsInstallsFailedResponse getAdminReportsInstallsFailed(Boolean expanded) {
    return FailedInstallList.generateFailedInstallReport(expanded);
  }

  @Override
  public GetAdminReportsObjectStoreListAllResponse getAdminReportsObjectStoreListAll() {
    return ObjectStoreList.listAllS3Objects();
  }
}
