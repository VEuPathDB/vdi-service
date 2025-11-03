package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("value")
public class DatasetTypePatchImpl implements DatasetTypePatch {
  @JsonProperty("value")
  private DatasetTypeInput value;

  @JsonProperty("value")
  public DatasetTypeInput getValue() {
    return this.value;
  }

  @JsonProperty("value")
  public void setValue(DatasetTypeInput value) {
    this.value = value;
  }
}
