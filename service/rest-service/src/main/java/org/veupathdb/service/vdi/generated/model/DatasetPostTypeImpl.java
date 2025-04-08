package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "version"
})
public class DatasetPostTypeImpl implements DatasetPostType {
  @JsonProperty(JsonField.NAME)
  private String name;

  @JsonProperty(JsonField.VERSION)
  private String version;

  @JsonProperty(JsonField.NAME)
  public String getName() {
    return this.name;
  }

  @JsonProperty(JsonField.NAME)
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty(JsonField.VERSION)
  public String getVersion() {
    return this.version;
  }

  @JsonProperty(JsonField.VERSION)
  public void setVersion(String version) {
    this.version = version;
  }
}
