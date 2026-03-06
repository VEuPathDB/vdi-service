package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = InstallTargetImpl.class
)
public interface InstallTarget {
  @JsonProperty(JsonField.ID)
  String getId();

  @JsonProperty(JsonField.ID)
  void setId(String id);

  @JsonProperty(JsonField.NAME)
  String getName();

  @JsonProperty(JsonField.NAME)
  void setName(String name);
}
