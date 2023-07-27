package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetFileDetailsImpl.class
)
public interface DatasetFileDetails {
  @JsonProperty("name")
  String getName();

  @JsonProperty("name")
  void setName(String name);

  @JsonProperty("size")
  Long getSize();

  @JsonProperty("size")
  void setSize(Long size);
}
