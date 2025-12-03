package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "import",
    "install"
})
public class DatasetStatusInfoImpl implements DatasetStatusInfo {
  @JsonProperty(JsonField.IMPORT)
  private DatasetImportStatusDetails importVariable;

  @JsonProperty(JsonField.INSTALL)
  private List<DatasetInstallStatusListEntry> install;

  @JsonProperty(JsonField.IMPORT)
  public DatasetImportStatusDetails getImport() {
    return this.importVariable;
  }

  @JsonProperty(JsonField.IMPORT)
  public void setImport(DatasetImportStatusDetails importVariable) {
    this.importVariable = importVariable;
  }

  @JsonProperty(JsonField.INSTALL)
  public List<DatasetInstallStatusListEntry> getInstall() {
    return this.install;
  }

  @JsonProperty(JsonField.INSTALL)
  public void setInstall(List<DatasetInstallStatusListEntry> install) {
    this.install = install;
  }
}
