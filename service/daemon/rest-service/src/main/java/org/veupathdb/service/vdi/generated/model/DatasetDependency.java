package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetDependencyImpl.class
)
public interface DatasetDependency {
  @JsonProperty("resourceIdentifier")
  String getResourceIdentifier();

  @JsonProperty("resourceIdentifier")
  void setResourceIdentifier(String resourceIdentifier);

  @JsonProperty("resourceDisplayName")
  String getResourceDisplayName();

  @JsonProperty("resourceDisplayName")
  void setResourceDisplayName(String resourceDisplayName);

  @JsonProperty("resourceVersion")
  String getResourceVersion();

  @JsonProperty("resourceVersion")
  void setResourceVersion(String resourceVersion);
}
