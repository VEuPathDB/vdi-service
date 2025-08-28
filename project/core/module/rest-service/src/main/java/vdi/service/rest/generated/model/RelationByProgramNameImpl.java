package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("program-name")
@JsonPropertyOrder("type")
public class RelationByProgramNameImpl implements RelationByProgramName {
  @JsonProperty(JsonField.TYPE)
  private final RelationByProgramName.TypeType type = _DISCRIMINATOR_TYPE_NAME;

  @JsonProperty(JsonField.TYPE)
  public RelationByProgramName.TypeType getType() {
    return this.type;
  }
}
