package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("value")
public class OrganismPatchImpl implements OrganismPatch {
  @JsonProperty("value")
  private DatasetOrganism value;

  @JsonProperty("value")
  public DatasetOrganism getValue() {
    return this.value;
  }

  @JsonProperty("value")
  public void setValue(DatasetOrganism value) {
    this.value = value;
  }
}
