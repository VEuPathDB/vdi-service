package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("project-name")
@JsonPropertyOrder("type")
public class RelationByProjectNameImpl implements RelationByProjectName {
  @JsonProperty(JsonField.TYPE)
  private final RelationByProjectName.TypeType type = _DISCRIMINATOR_TYPE_NAME;

  @JsonProperty(JsonField.TYPE)
  public RelationByProjectName.TypeType getType() {
    return this.type;
  }
}
