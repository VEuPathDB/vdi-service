package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetPublicationImpl.class
)
public interface DatasetPublication {
  @JsonProperty(JsonField.PUB_MED_ID)
  String getPubMedId();

  @JsonProperty(JsonField.PUB_MED_ID)
  void setPubMedId(String pubMedId);

  @JsonProperty(JsonField.CITATION)
  String getCitation();

  @JsonProperty(JsonField.CITATION)
  void setCitation(String citation);

  @JsonProperty(
      value = JsonField.IS_PRIMARY,
      defaultValue = "false"
  )
  Boolean getIsPrimary();

  @JsonProperty(
      value = JsonField.IS_PRIMARY,
      defaultValue = "false"
  )
  void setIsPrimary(Boolean isPrimary);
}
