package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("value")
public class OptionalStringPatchImpl implements OptionalStringPatch {
  @JsonProperty("value")
  private String value;

  @JsonProperty("value")
  public String getValue() {
    return this.value;
  }

  @JsonProperty("value")
  public void setValue(String value) {
    this.value = value;
  }
}
