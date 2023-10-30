package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetFileDetailsImpl.class
)
public interface DatasetFileDetails {
  @JsonProperty("fileName")
  String getFileName();

  @JsonProperty("fileName")
  void setFileName(String fileName);

  @JsonProperty("fileSize")
  Long getFileSize();

  @JsonProperty("fileSize")
  void setFileSize(Long fileSize);
}
