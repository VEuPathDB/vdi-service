package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetStatusInfoImpl.class
)
public interface DatasetStatusInfo {
  @JsonProperty(JsonField.IMPORT)
  DatasetImportStatus getImport();

  @JsonProperty(JsonField.IMPORT)
  void setImport(DatasetImportStatus importVariable);

  @JsonProperty(JsonField.INSTALL)
  List<DatasetInstallStatusEntry> getInstall();

  @JsonProperty(JsonField.INSTALL)
  void setInstall(List<DatasetInstallStatusEntry> install);
}
