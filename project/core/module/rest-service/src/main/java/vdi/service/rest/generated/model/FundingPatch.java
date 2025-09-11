package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = FundingPatchImpl.class
)
public interface FundingPatch {
  @JsonProperty("value")
  List<DatasetFundingAward> getValue();

  @JsonProperty("value")
  void setValue(List<DatasetFundingAward> value);
}
