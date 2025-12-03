package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetStatusInfoImpl.class
)
public interface DatasetStatusInfo {
  @JsonProperty(JsonField.IMPORT)
  DatasetImportStatusDetails getImport();

  @JsonProperty(JsonField.IMPORT)
  void setImport(DatasetImportStatusDetails importVariable);

  @JsonProperty(JsonField.INSTALL)
  List<DatasetInstallStatusListEntry> getInstall();

  @JsonProperty(JsonField.INSTALL)
  void setInstall(List<DatasetInstallStatusListEntry> install);
}
