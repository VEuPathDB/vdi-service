package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetFundingAwardImpl.class
)
public interface DatasetFundingAward {
  @JsonProperty(JsonField.AGENCY)
  String getAgency();

  @JsonProperty(JsonField.AGENCY)
  void setAgency(String agency);

  @JsonProperty(JsonField.AWARD_NUMBER)
  String getAwardNumber();

  @JsonProperty(JsonField.AWARD_NUMBER)
  void setAwardNumber(String awardNumber);
}
