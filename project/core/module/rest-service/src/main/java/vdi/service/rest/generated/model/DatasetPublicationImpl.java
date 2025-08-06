package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "identifier",
    "type",
    "citation",
    "isPrimary"
})
public class DatasetPublicationImpl implements DatasetPublication {
  @JsonProperty(JsonField.IDENTIFIER)
  private String identifier;

  @JsonProperty(
      value = JsonField.TYPE,
      defaultValue = "pmid"
  )
  private DatasetPublication.TypeType type;

  @JsonProperty(JsonField.CITATION)
  private String citation;

  @JsonProperty(
      value = JsonField.IS_PRIMARY,
      defaultValue = "false"
  )
  private Boolean isPrimary;

  @JsonProperty(JsonField.IDENTIFIER)
  public String getIdentifier() {
    return this.identifier;
  }

  @JsonProperty(JsonField.IDENTIFIER)
  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  @JsonProperty(
      value = JsonField.TYPE,
      defaultValue = "pmid"
  )
  public DatasetPublication.TypeType getType() {
    return this.type;
  }

  @JsonProperty(
      value = JsonField.TYPE,
      defaultValue = "pmid"
  )
  public void setType(DatasetPublication.TypeType type) {
    this.type = type;
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
