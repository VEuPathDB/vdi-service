package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DOIReferenceImpl.class
)
public interface DOIReference {
  @JsonProperty(JsonField.DOI)
  String getDoi();

  @JsonProperty(JsonField.DOI)
  void setDoi(String doi);

  @JsonProperty(JsonField.DESCRIPTION)
  String getDescription();

  @JsonProperty(JsonField.DESCRIPTION)
  void setDescription(String description);
}
