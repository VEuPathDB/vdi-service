package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetTypeInfoImpl.class
)
public interface DatasetTypeInfo {
  @JsonProperty(JsonField.NAME)
  String getName();

  @JsonProperty(JsonField.NAME)
  void setName(String name);

  @JsonProperty(JsonField.VERSION)
  String getVersion();

  @JsonProperty(JsonField.VERSION)
  void setVersion(String version);

  @JsonProperty(JsonField.DISPLAY_NAME)
  String getDisplayName();

  @JsonProperty(JsonField.DISPLAY_NAME)
  void setDisplayName(String displayName);
}
