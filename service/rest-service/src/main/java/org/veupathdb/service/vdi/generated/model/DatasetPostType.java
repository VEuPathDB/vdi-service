package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetPostTypeImpl.class
)
public interface DatasetPostType {
  @JsonProperty(JsonField.NAME)
  String getName();

  @JsonProperty(JsonField.NAME)
  void setName(String name);

  @JsonProperty(JsonField.VERSION)
  String getVersion();

  @JsonProperty(JsonField.VERSION)
  void setVersion(String version);
}
