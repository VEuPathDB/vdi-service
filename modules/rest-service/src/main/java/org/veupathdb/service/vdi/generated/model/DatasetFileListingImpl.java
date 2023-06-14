package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "uploadFiles",
    "dataFiles"
})
public class DatasetFileListingImpl implements DatasetFileListing {
  @JsonProperty("uploadFiles")
  private List<DatasetFileDetails> uploadFiles;

  @JsonProperty("dataFiles")
  private List<DatasetFileDetails> dataFiles;

  @JsonProperty("uploadFiles")
  public List<DatasetFileDetails> getUploadFiles() {
    return this.uploadFiles;
  }

  @JsonProperty("uploadFiles")
  public void setUploadFiles(List<DatasetFileDetails> uploadFiles) {
    this.uploadFiles = uploadFiles;
  }

  @JsonProperty("dataFiles")
  public List<DatasetFileDetails> getDataFiles() {
    return this.dataFiles;
  }

  @JsonProperty("dataFiles")
  public void setDataFiles(List<DatasetFileDetails> dataFiles) {
    this.dataFiles = dataFiles;
  }
}
