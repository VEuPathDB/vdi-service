package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "agency",
    "awardNumber"
})
public class DatasetFundingAwardImpl implements DatasetFundingAward {
  @JsonProperty(JsonField.AGENCY)
  private String agency;

  @JsonProperty(JsonField.AWARD_NUMBER)
  private String awardNumber;

  @JsonProperty(JsonField.AGENCY)
  public String getAgency() {
    return this.agency;
  }

  @JsonProperty(JsonField.AGENCY)
  public void setAgency(String agency) {
    this.agency = agency;
  }

  @JsonProperty(JsonField.AWARD_NUMBER)
  public String getAwardNumber() {
    return this.awardNumber;
  }

  @JsonProperty(JsonField.AWARD_NUMBER)
  public void setAwardNumber(String awardNumber) {
    this.awardNumber = awardNumber;
  }
}
