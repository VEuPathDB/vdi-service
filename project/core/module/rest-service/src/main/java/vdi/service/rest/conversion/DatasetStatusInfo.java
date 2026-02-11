package vdi.service.rest.conversion;

import vdi.service.rest.generated.model.DatasetImportStatusInfo;
import vdi.service.rest.generated.model.DatasetInstallStatusListEntry;
import vdi.service.rest.generated.model.DatasetStatusInfoImpl;
import vdi.service.rest.generated.model.DatasetUploadStatusInfo;

import java.util.List;
import java.util.Objects;

public class DatasetStatusInfo extends DatasetStatusInfoImpl {
  public DatasetStatusInfo(
    DatasetUploadStatusInfo uploadStatus,
    DatasetImportStatusInfo importStatus,
    List<DatasetInstallStatusListEntry> installStatus
  ) {
    setUpload(Objects.requireNonNull(uploadStatus));
    setImport(importStatus);
    setInstall(installStatus);
  }
}
