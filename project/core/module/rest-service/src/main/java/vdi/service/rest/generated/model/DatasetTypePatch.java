package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetTypePatchImpl.class
)
public interface DatasetTypePatch {
  @JsonProperty("value")
  DatasetTypeInput getValue();

  @JsonProperty("value")
  void setValue(DatasetTypeInput value);
}
