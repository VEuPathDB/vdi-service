package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("type")
public class ImplicitRelationImpl implements ImplicitRelation {
  @JsonProperty(JsonField.TYPE)
  private final ImplicitRelation.TypeType type = _DISCRIMINATOR_TYPE_NAME;

  @JsonProperty(JsonField.TYPE)
  public ImplicitRelation.TypeType getType() {
    return this.type;
  }
}
