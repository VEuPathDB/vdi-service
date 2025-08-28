package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeName("publication")
@JsonDeserialize(
    as = RelationByPublicationImpl.class
)
public interface RelationByPublication extends ImplicitRelation {
  DatasetPublicationType _DISCRIMINATOR_TYPE_NAME = DatasetPublicationType.PUBLICATION;

  @JsonProperty(JsonField.TYPE)
  DatasetPublicationType getType();

  @JsonProperty(JsonField.IDENTIFIER)
  String getIdentifier();

  @JsonProperty(JsonField.IDENTIFIER)
  void setIdentifier(String identifier);
}
