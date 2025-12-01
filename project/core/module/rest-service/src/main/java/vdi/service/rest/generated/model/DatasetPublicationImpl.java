package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "identifier",
    "type",
    "isPrimary"
})
public class DatasetPublicationImpl implements DatasetPublication {
  @JsonProperty(JsonField.IDENTIFIER)
  private String identifier;

  @JsonProperty(JsonField.TYPE)
  private DatasetPublicationType type;

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

  @JsonProperty(JsonField.TYPE)
  public DatasetPublicationType getType() {
    return this.type;
  }

  @JsonProperty(JsonField.TYPE)
  public void setType(DatasetPublicationType type) {
    this.type = type;
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
