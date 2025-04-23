package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetTypeRequestBodyImpl.class
)
public interface DatasetTypeRequestBody {
  @JsonProperty(JsonField.NAME)
  String getName();

  @JsonProperty(JsonField.NAME)
  void setName(String name);

  @JsonProperty(JsonField.VERSION)
  String getVersion();

  @JsonProperty(JsonField.VERSION)
  void setVersion(String version);
}
