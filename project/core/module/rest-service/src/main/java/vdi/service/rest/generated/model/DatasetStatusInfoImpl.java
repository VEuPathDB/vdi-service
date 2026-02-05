package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "upload",
    "import",
    "install"
})
public class DatasetStatusInfoImpl implements DatasetStatusInfo {
  @JsonProperty(JsonField.UPLOAD)
  private DatasetUploadStatusInfo upload;

  @JsonProperty(JsonField.IMPORT)
  private DatasetImportStatusInfo importVariable;

  @JsonProperty(JsonField.INSTALL)
  private List<DatasetInstallStatusListEntry> install;

  @JsonProperty(JsonField.UPLOAD)
  public DatasetUploadStatusInfo getUpload() {
    return this.upload;
  }

  @JsonProperty(JsonField.UPLOAD)
  public void setUpload(DatasetUploadStatusInfo upload) {
    this.upload = upload;
  }

  @JsonProperty(JsonField.IMPORT)
  public DatasetImportStatusInfo getImport() {
    return this.importVariable;
  }

  @JsonProperty(JsonField.IMPORT)
  public void setImport(DatasetImportStatusInfo importVariable) {
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
