package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "resourceIdentifier",
    "resourceDisplayName",
    "resourceVersion"
})
public class DatasetDependencyImpl implements DatasetDependency {
  @JsonProperty("resourceIdentifier")
  private String resourceIdentifier;

  @JsonProperty("resourceDisplayName")
  private String resourceDisplayName;

  @JsonProperty("resourceVersion")
  private String resourceVersion;

  @JsonProperty("resourceIdentifier")
  public String getResourceIdentifier() {
    return this.resourceIdentifier;
  }

  @JsonProperty("resourceIdentifier")
  public void setResourceIdentifier(String resourceIdentifier) {
    this.resourceIdentifier = resourceIdentifier;
  }

  @JsonProperty("resourceDisplayName")
  public String getResourceDisplayName() {
    return this.resourceDisplayName;
  }

  @JsonProperty("resourceDisplayName")
  public void setResourceDisplayName(String resourceDisplayName) {
    this.resourceDisplayName = resourceDisplayName;
  }

  @JsonProperty("resourceVersion")
  public String getResourceVersion() {
    return this.resourceVersion;
  }

  @JsonProperty("resourceVersion")
  public void setResourceVersion(String resourceVersion) {
    this.resourceVersion = resourceVersion;
  }
}
