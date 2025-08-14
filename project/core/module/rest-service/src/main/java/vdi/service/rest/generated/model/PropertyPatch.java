package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;

@JsonDeserialize(
    as = PropertyPatchImpl.class
)
public interface PropertyPatch {
  @JsonProperty(JsonField.ACTION)
  PatchAction getAction();

  @JsonProperty(JsonField.ACTION)
  void setAction(PatchAction action);

  @JsonAnyGetter
  Map<String, Object> getAdditionalProperties();

  @JsonAnySetter
  void setAdditionalProperties(String key, Object value);
}
