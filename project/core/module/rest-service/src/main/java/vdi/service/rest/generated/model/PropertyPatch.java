package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = PropertyPatchImpl.class
)
public interface PropertyPatch {
  @JsonProperty(JsonField.ACTION)
  PatchAction getAction();

  @JsonProperty(JsonField.ACTION)
  void setAction(PatchAction action);
}
