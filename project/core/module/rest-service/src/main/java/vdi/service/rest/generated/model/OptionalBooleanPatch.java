package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = OptionalBooleanPatchImpl.class
)
public interface OptionalBooleanPatch {
  @JsonProperty("value")
  Boolean getValue();

  @JsonProperty("value")
  void setValue(Boolean value);
}
