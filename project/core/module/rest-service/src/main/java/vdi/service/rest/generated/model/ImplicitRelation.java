package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "relationType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(vdi.service.rest.generated.model.RelationByPublication.class),
    @JsonSubTypes.Type(vdi.service.rest.generated.model.RelationByProjectName.class),
    @JsonSubTypes.Type(vdi.service.rest.generated.model.RelationByProgramName.class),
    @JsonSubTypes.Type(vdi.service.rest.generated.model.ImplicitRelation.class)
})
@JsonDeserialize(
    as = ImplicitRelationImpl.class
)
public interface ImplicitRelation {
  ImplicitRelationType _DISCRIMINATOR_TYPE_NAME = null;

  @JsonProperty(JsonField.RELATION_TYPE)
  ImplicitRelationType getRelationType();
}
