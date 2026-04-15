package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("value")
public class DatasetSourcesPatchImpl implements DatasetSourcesPatch {
  @JsonProperty("value")
  private List<DatasetSource> value;

  @JsonProperty("value")
  public List<DatasetSource> getValue() {
    return this.value;
  }

  @JsonProperty("value")
  public void setValue(List<DatasetSource> value) {
    this.value = value;
  }
}
