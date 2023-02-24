package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetTypeInfoImpl.class
)
public interface DatasetTypeInfo {
  @JsonProperty("name")
  String getName();

  @JsonProperty("name")
  void setName(String name);

  @JsonProperty("version")
  String getVersion();

  @JsonProperty("version")
  void setVersion(String version);
}
