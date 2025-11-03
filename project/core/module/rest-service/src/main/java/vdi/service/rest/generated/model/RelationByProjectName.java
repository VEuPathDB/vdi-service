package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeName("project-name")
@JsonDeserialize(
    as = RelationByProjectNameImpl.class
)
public interface RelationByProjectName extends ImplicitRelation {
  ImplicitRelationType _DISCRIMINATOR_TYPE_NAME = ImplicitRelationType.PROJECTNAME;

  @JsonProperty(JsonField.RELATION_TYPE)
  ImplicitRelationType getRelationType();
}
