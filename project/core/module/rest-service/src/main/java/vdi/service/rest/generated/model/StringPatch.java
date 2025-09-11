package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = StringPatchImpl.class
)
public interface StringPatch {
  @JsonProperty("value")
  String getValue();

  @JsonProperty("value")
  void setValue(String value);
}
