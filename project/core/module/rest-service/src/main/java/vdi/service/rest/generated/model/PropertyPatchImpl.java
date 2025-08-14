package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("action")
public class PropertyPatchImpl implements PropertyPatch {
  @JsonProperty(JsonField.ACTION)
  private PatchAction action;

  @JsonProperty(JsonField.ACTION)
  public PatchAction getAction() {
    return this.action;
  }

  @JsonProperty(JsonField.ACTION)
  public void setAction(PatchAction action) {
    this.action = action;
  }
}
