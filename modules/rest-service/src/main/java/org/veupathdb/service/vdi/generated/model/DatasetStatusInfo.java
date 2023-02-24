package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetStatusInfoImpl.class
)
public interface DatasetStatusInfo {
  @JsonProperty("import")
  DatasetImportStatus getImport();

  @JsonProperty("import")
  void setImport(DatasetImportStatus importVariable);

  @JsonProperty("install")
  List<DatasetInstallStatusEntry> getInstall();

  @JsonProperty("install")
  void setInstall(List<DatasetInstallStatusEntry> install);
}
