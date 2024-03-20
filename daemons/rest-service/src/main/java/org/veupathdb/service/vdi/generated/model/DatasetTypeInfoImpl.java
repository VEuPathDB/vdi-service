package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "displayName",
    "version"
})
public class DatasetTypeInfoImpl implements DatasetTypeInfo {
  @JsonProperty("name")
  private String name;

  @JsonProperty("displayName")
  private String displayName;

  @JsonProperty("version")
  private String version;

  @JsonProperty("name")
  public String getName() {
    return this.name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("displayName")
  public String getDisplayName() {
    return this.displayName;
  }

  @JsonProperty("displayName")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @JsonProperty("version")
  public String getVersion() {
    return this.version;
  }

  @JsonProperty("version")
  public void setVersion(String version) {
    this.version = version;
  }
}
