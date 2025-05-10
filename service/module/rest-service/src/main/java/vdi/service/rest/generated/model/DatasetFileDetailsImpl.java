package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fileName",
    "fileSize"
})
public class DatasetFileDetailsImpl implements DatasetFileDetails {
  @JsonProperty(JsonField.FILE_NAME)
  private String fileName;

  @JsonProperty(JsonField.FILE_SIZE)
  private Long fileSize;

  @JsonProperty(JsonField.FILE_NAME)
  public String getFileName() {
    return this.fileName;
  }

  @JsonProperty(JsonField.FILE_NAME)
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  @JsonProperty(JsonField.FILE_SIZE)
  public Long getFileSize() {
    return this.fileSize;
  }

  @JsonProperty(JsonField.FILE_SIZE)
  public void setFileSize(Long fileSize) {
    this.fileSize = fileSize;
  }
}
