package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("value")
public class OrganismsPatchImpl implements OrganismsPatch {
  @JsonProperty("value")
  private List<DatasetOrganism> value;

  @JsonProperty("value")
  public List<DatasetOrganism> getValue() {
    return this.value;
  }

  @JsonProperty("value")
  public void setValue(List<DatasetOrganism> value) {
    this.value = value;
  }
}
