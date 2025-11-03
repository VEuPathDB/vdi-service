package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("publication")
@JsonPropertyOrder({
    "relationType",
    "identifier",
    "type"
})
public class RelationByPublicationImpl implements RelationByPublication {
  @JsonProperty(JsonField.RELATION_TYPE)
  private final ImplicitRelationType relationType = _DISCRIMINATOR_TYPE_NAME;

  @JsonProperty(JsonField.IDENTIFIER)
  private String identifier;

  @JsonProperty(JsonField.TYPE)
  private DatasetPublicationType type;

  @JsonProperty(JsonField.RELATION_TYPE)
  public ImplicitRelationType getRelationType() {
    return this.relationType;
  }

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
}
