package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = OptionalIntPatchImpl.class
)
public interface OptionalIntPatch {
  @JsonProperty("value")
  Integer getValue();

  @JsonProperty("value")
  void setValue(Integer value);
}
