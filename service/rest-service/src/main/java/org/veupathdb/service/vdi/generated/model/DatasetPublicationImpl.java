package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "citation",
    "pubMedId"
})
public class DatasetPublicationImpl implements DatasetPublication {
  @JsonProperty("citation")
  private String citation;

  @JsonProperty("pubMedId")
  private String pubMedId;

  @JsonProperty("citation")
  public String getCitation() {
    return this.citation;
  }

  @JsonProperty("citation")
  public void setCitation(String citation) {
    this.citation = citation;
  }

  @JsonProperty("pubMedId")
  public String getPubMedId() {
    return this.pubMedId;
  }

  @JsonProperty("pubMedId")
  public void setPubMedId(String pubMedId) {
    this.pubMedId = pubMedId;
  }
}
