package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetTypeInputImpl.class
)
public interface DatasetTypeInput {
  @JsonProperty(JsonField.NAME)
  String getName();

  @JsonProperty(JsonField.NAME)
  void setName(String name);

  @JsonProperty(JsonField.VERSION)
  String getVersion();

  @JsonProperty(JsonField.VERSION)
  void setVersion(String version);
}
