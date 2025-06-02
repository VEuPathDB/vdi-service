package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "upload",
    "install"
})
public class DatasetFileListingImpl implements DatasetFileListing {
  @JsonProperty(JsonField.UPLOAD)
  private DatasetZipDetails upload;

  @JsonProperty(JsonField.INSTALL)
  private DatasetZipDetails install;

  @JsonProperty(JsonField.UPLOAD)
  public DatasetZipDetails getUpload() {
    return this.upload;
  }

  @JsonProperty(JsonField.UPLOAD)
  public void setUpload(DatasetZipDetails upload) {
    this.upload = upload;
  }

  @JsonProperty(JsonField.INSTALL)
  public DatasetZipDetails getInstall() {
    return this.install;
  }

  @JsonProperty(JsonField.INSTALL)
  public void setInstall(DatasetZipDetails install) {
    this.install = install;
  }
}
