package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "size"
})
public class DatasetFileDetailsImpl implements DatasetFileDetails {
  @JsonProperty("name")
  private String name;

  @JsonProperty("size")
  private Long size;

  @JsonProperty("name")
  public String getName() {
    return this.name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("size")
  public Long getSize() {
    return this.size;
  }

  @JsonProperty("size")
  public void setSize(Long size) {
    this.size = size;
  }
}
