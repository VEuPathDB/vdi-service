package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("value")
public class ContactsPatchImpl implements ContactsPatch {
  @JsonProperty("value")
  private List<DatasetContact> value;

  @JsonProperty("value")
  public List<DatasetContact> getValue() {
    return this.value;
  }

  @JsonProperty("value")
  public void setValue(List<DatasetContact> value) {
    this.value = value;
  }
}
