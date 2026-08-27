package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("value")
public class OptionalBooleanPatchImpl implements OptionalBooleanPatch {
  @JsonProperty("value")
  private Boolean value;

  @JsonProperty("value")
  public Boolean getValue() {
    return this.value;
  }

  @JsonProperty("value")
  public void setValue(Boolean value) {
    this.value = value;
  }
}
