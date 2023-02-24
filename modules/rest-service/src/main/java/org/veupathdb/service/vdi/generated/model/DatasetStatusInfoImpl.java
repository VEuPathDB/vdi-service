package org.veupathdb.service.vdi.generated.model;

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
  @JsonProperty("import")
  private DatasetImportStatus importVariable;

  @JsonProperty("install")
  private List<DatasetInstallStatusEntry> install;

  @JsonProperty("import")
  public DatasetImportStatus getImport() {
    return this.importVariable;
  }

  @JsonProperty("import")
  public void setImport(DatasetImportStatus importVariable) {
    this.importVariable = importVariable;
  }

  @JsonProperty("install")
  public List<DatasetInstallStatusEntry> getInstall() {
    return this.install;
  }

  @JsonProperty("install")
  public void setInstall(List<DatasetInstallStatusEntry> install) {
    this.install = install;
  }
}
