package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = VisibilityPatchImpl.class
)
public interface VisibilityPatch {
  @JsonProperty("value")
  DatasetVisibility getValue();

  @JsonProperty("value")
  void setValue(DatasetVisibility value);
}
