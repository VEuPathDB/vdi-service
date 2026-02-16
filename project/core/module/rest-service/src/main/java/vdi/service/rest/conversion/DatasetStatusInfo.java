package vdi.service.rest.conversion;

import vdi.core.db.app.model.InstallStatuses;
import vdi.core.db.cache.model.DatasetImportStatus;
import vdi.model.DatasetUploadStatus;
import vdi.service.rest.generated.model.DatasetStatusInfoImpl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DatasetStatusInfo extends DatasetStatusInfoImpl {
  public DatasetStatusInfo(
    vdi.service.rest.generated.model.DatasetUploadStatusInfo uploadStatus,
    vdi.service.rest.generated.model.DatasetImportStatusInfo importStatus,
    List<vdi.service.rest.generated.model.DatasetInstallStatusListEntry> installStatus
  ) {
    setUpload(Objects.requireNonNull(uploadStatus));
    setImport(importStatus);
    setInstall(installStatus);
  }

  public DatasetStatusInfo(
    DatasetUploadStatus uploadStatus,
    DatasetImportStatus importStatus,
    Map<String, InstallStatuses> installStatuses
  ) {
    this(
      new DatasetUploadStatusInfo(uploadStatus, null),
      importStatus == null
        ? null
        : new DatasetImportStatusInfo(importStatus, null),
      installStatuses.entrySet()
        .stream()
        .map(DatasetInstallStatusListEntry::new)
        .map(vdi.service.rest.generated.model.DatasetInstallStatusListEntry.class::cast)
        .toList()
    );
  }
}
