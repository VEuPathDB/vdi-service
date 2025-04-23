package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "pubMedId",
    "citation",
    "isPrimary"
})
public class DatasetPublicationImpl implements DatasetPublication {
  @JsonProperty(JsonField.PUB_MED_ID)
  private String pubMedId;

  @JsonProperty(JsonField.CITATION)
  private String citation;

  @JsonProperty(
      value = JsonField.IS_PRIMARY,
      defaultValue = "false"
  )
  private Boolean isPrimary;

  @JsonProperty(JsonField.PUB_MED_ID)
  public String getPubMedId() {
    return this.pubMedId;
  }

  @JsonProperty(JsonField.PUB_MED_ID)
  public void setPubMedId(String pubMedId) {
    this.pubMedId = pubMedId;
  }

  @JsonProperty(JsonField.CITATION)
  public String getCitation() {
    return this.citation;
  }

  @JsonProperty(JsonField.CITATION)
  public void setCitation(String citation) {
    this.citation = citation;
  }

  @JsonProperty(
      value = JsonField.IS_PRIMARY,
      defaultValue = "false"
  )
  public Boolean getIsPrimary() {
    return this.isPrimary;
  }

  @JsonProperty(
      value = JsonField.IS_PRIMARY,
      defaultValue = "false"
  )
  public void setIsPrimary(Boolean isPrimary) {
    this.isPrimary = isPrimary;
  }
}
