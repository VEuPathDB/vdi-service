package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("value")
public class PublicationsPatchImpl implements PublicationsPatch {
  @JsonProperty("value")
  private List<DatasetPublication> value;

  @JsonProperty("value")
  public List<DatasetPublication> getValue() {
    return this.value;
  }

  @JsonProperty("value")
  public void setValue(List<DatasetPublication> value) {
    this.value = value;
  }
}
