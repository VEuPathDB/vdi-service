package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fileName",
    "fileSize"
})
public class DatasetFileDetailsImpl implements DatasetFileDetails {
  @JsonProperty("fileName")
  private String fileName;

  @JsonProperty("fileSize")
  private Long fileSize;

  @JsonProperty("fileName")
  public String getFileName() {
    return this.fileName;
  }

  @JsonProperty("fileName")
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  @JsonProperty("fileSize")
  public Long getFileSize() {
    return this.fileSize;
  }

  @JsonProperty("fileSize")
  public void setFileSize(Long fileSize) {
    this.fileSize = fileSize;
  }
}
