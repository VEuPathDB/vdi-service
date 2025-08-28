package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeName("program-name")
@JsonDeserialize(
    as = RelationByProgramNameImpl.class
)
public interface RelationByProgramName extends ImplicitRelation {
  ImplicitRelationType _DISCRIMINATOR_TYPE_NAME = ImplicitRelationType.PROGRAMNAME;

  @JsonProperty(JsonField.RELATION_TYPE)
  ImplicitRelationType getRelationType();
}
