package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("program-name")
@JsonPropertyOrder("relationType")
public class RelationByProgramNameImpl implements RelationByProgramName {
  @JsonProperty(JsonField.RELATION_TYPE)
  private final ImplicitRelationType relationType = _DISCRIMINATOR_TYPE_NAME;

  @JsonProperty(JsonField.RELATION_TYPE)
  public ImplicitRelationType getRelationType() {
    return this.relationType;
  }
}
