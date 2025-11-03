package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = BioprojectIDReferenceImpl.class
)
public interface BioprojectIDReference {
  @JsonProperty(JsonField.ID)
  String getId();

  @JsonProperty(JsonField.ID)
  void setId(String id);

  @JsonProperty(JsonField.DESCRIPTION)
  String getDescription();

  @JsonProperty(JsonField.DESCRIPTION)
  void setDescription(String description);
}
