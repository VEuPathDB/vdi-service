package vdi.service.rest.generated.model;

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
  @JsonProperty(JsonField.RESOURCE_IDENTIFIER)
  private String resourceIdentifier;

  @JsonProperty(JsonField.RESOURCE_DISPLAY_NAME)
  private String resourceDisplayName;

  @JsonProperty(JsonField.RESOURCE_VERSION)
  private String resourceVersion;

  @JsonProperty(JsonField.RESOURCE_IDENTIFIER)
  public String getResourceIdentifier() {
    return this.resourceIdentifier;
  }

  @JsonProperty(JsonField.RESOURCE_IDENTIFIER)
  public void setResourceIdentifier(String resourceIdentifier) {
    this.resourceIdentifier = resourceIdentifier;
  }

  @JsonProperty(JsonField.RESOURCE_DISPLAY_NAME)
  public String getResourceDisplayName() {
    return this.resourceDisplayName;
  }

  @JsonProperty(JsonField.RESOURCE_DISPLAY_NAME)
  public void setResourceDisplayName(String resourceDisplayName) {
    this.resourceDisplayName = resourceDisplayName;
  }

  @JsonProperty(JsonField.RESOURCE_VERSION)
  public String getResourceVersion() {
    return this.resourceVersion;
  }

  @JsonProperty(JsonField.RESOURCE_VERSION)
  public void setResourceVersion(String resourceVersion) {
    this.resourceVersion = resourceVersion;
  }
}
