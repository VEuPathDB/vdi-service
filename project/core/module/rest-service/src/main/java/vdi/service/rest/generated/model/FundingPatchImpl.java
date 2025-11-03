package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("value")
public class FundingPatchImpl implements FundingPatch {
  @JsonProperty("value")
  private List<DatasetFundingAward> value;

  @JsonProperty("value")
  public List<DatasetFundingAward> getValue() {
    return this.value;
  }

  @JsonProperty("value")
  public void setValue(List<DatasetFundingAward> value) {
    this.value = value;
  }
}
