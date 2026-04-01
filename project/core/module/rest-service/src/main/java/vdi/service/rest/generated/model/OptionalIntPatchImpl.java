package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("value")
public class OptionalIntPatchImpl implements OptionalIntPatch {
  @JsonProperty("value")
  private Integer value;

  @JsonProperty("value")
  public Integer getValue() {
    return this.value;
  }

  @JsonProperty("value")
  public void setValue(Integer value) {
    this.value = value;
  }
}
