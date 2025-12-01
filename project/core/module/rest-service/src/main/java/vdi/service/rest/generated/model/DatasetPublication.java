package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetPublicationImpl.class
)
public interface DatasetPublication {
  @JsonProperty(JsonField.IDENTIFIER)
  String getIdentifier();

  @JsonProperty(JsonField.IDENTIFIER)
  void setIdentifier(String identifier);

  @JsonProperty(JsonField.TYPE)
  DatasetPublicationType getType();

  @JsonProperty(JsonField.TYPE)
  void setType(DatasetPublicationType type);

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
