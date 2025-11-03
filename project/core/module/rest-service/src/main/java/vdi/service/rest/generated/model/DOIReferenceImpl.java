package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "doi",
    "description"
})
public class DOIReferenceImpl implements DOIReference {
  @JsonProperty(JsonField.DOI)
  private String doi;

  @JsonProperty(JsonField.DESCRIPTION)
  private String description;

  @JsonProperty(JsonField.DOI)
  public String getDoi() {
    return this.doi;
  }

  @JsonProperty(JsonField.DOI)
  public void setDoi(String doi) {
    this.doi = doi;
  }

  @JsonProperty(JsonField.DESCRIPTION)
  public String getDescription() {
    return this.description;
  }

  @JsonProperty(JsonField.DESCRIPTION)
  public void setDescription(String description) {
    this.description = description;
  }
}
