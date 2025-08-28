package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("relationType")
public class ImplicitRelationImpl implements ImplicitRelation {
  @JsonProperty(JsonField.RELATION_TYPE)
  private final ImplicitRelationType relationType = _DISCRIMINATOR_TYPE_NAME;

  @JsonProperty(JsonField.RELATION_TYPE)
  public ImplicitRelationType getRelationType() {
    return this.relationType;
  }
}
