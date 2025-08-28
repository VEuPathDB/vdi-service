package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("publication")
@JsonPropertyOrder({
    "type",
    "identifier"
})
public class RelationByPublicationImpl implements RelationByPublication {
  @JsonProperty(JsonField.TYPE)
  private final DatasetPublicationType type = _DISCRIMINATOR_TYPE_NAME;

  @JsonProperty(JsonField.IDENTIFIER)
  private String identifier;

  @JsonProperty(JsonField.TYPE)
  public DatasetPublicationType getType() {
    return this.type;
  }

  @JsonProperty(JsonField.IDENTIFIER)
  public String getIdentifier() {
    return this.identifier;
  }

  @JsonProperty(JsonField.IDENTIFIER)
  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }
}
