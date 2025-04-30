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
  private DatasetImportStatus importVariable;

  @JsonProperty(JsonField.INSTALL)
  private List<DatasetInstallStatusEntry> install;

  @JsonProperty(JsonField.IMPORT)
  public DatasetImportStatus getImport() {
    return this.importVariable;
  }

  @JsonProperty(JsonField.IMPORT)
  public void setImport(DatasetImportStatus importVariable) {
    this.importVariable = importVariable;
  }

  @JsonProperty(JsonField.INSTALL)
  public List<DatasetInstallStatusEntry> getInstall() {
    return this.install;
  }

  @JsonProperty(JsonField.INSTALL)
  public void setInstall(List<DatasetInstallStatusEntry> install) {
    this.install = install;
  }
}
