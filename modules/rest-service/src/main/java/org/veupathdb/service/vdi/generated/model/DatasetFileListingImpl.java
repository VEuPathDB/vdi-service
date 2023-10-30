package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "upload",
    "install"
})
public class DatasetFileListingImpl implements DatasetFileListing {
  @JsonProperty("upload")
  private DatasetZipDetails upload;

  @JsonProperty("install")
  private DatasetZipDetails install;

  @JsonProperty("upload")
  public DatasetZipDetails getUpload() {
    return this.upload;
  }

  @JsonProperty("upload")
  public void setUpload(DatasetZipDetails upload) {
    this.upload = upload;
  }

  @JsonProperty("install")
  public DatasetZipDetails getInstall() {
    return this.install;
  }

  @JsonProperty("install")
  public void setInstall(DatasetZipDetails install) {
    this.install = install;
  }
}
