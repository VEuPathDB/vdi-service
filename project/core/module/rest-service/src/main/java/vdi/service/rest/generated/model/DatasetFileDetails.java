package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetFileDetailsImpl.class
)
public interface DatasetFileDetails {
  @JsonProperty(JsonField.FILE_NAME)
  String getFileName();

  @JsonProperty(JsonField.FILE_NAME)
  void setFileName(String fileName);

  @JsonProperty(JsonField.FILE_SIZE)
  Long getFileSize();

  @JsonProperty(JsonField.FILE_SIZE)
  void setFileSize(Long fileSize);
}
