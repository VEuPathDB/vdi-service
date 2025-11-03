package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetDependencyImpl.class
)
public interface DatasetDependency {
  @JsonProperty(JsonField.RESOURCE_IDENTIFIER)
  String getResourceIdentifier();

  @JsonProperty(JsonField.RESOURCE_IDENTIFIER)
  void setResourceIdentifier(String resourceIdentifier);

  @JsonProperty(JsonField.RESOURCE_DISPLAY_NAME)
  String getResourceDisplayName();

  @JsonProperty(JsonField.RESOURCE_DISPLAY_NAME)
  void setResourceDisplayName(String resourceDisplayName);

  @JsonProperty(JsonField.RESOURCE_VERSION)
  String getResourceVersion();

  @JsonProperty(JsonField.RESOURCE_VERSION)
  void setResourceVersion(String resourceVersion);
}
