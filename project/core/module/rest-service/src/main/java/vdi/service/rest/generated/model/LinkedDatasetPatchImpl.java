package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("value")
public class LinkedDatasetPatchImpl implements LinkedDatasetPatch {
  @JsonProperty("value")
  private List<LinkedDataset> value;

  @JsonProperty("value")
  public List<LinkedDataset> getValue() {
    return this.value;
  }

  @JsonProperty("value")
  public void setValue(List<LinkedDataset> value) {
    this.value = value;
  }
}
