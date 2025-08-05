package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "op",
    "path",
    "value",
    "from"
})
public class JSONPatchActionImpl implements JSONPatchAction {
  @JsonProperty(JsonField.OP)
  private JSONPatchAction.OpType op;

  @JsonProperty(JsonField.PATH)
  private String path;

  @JsonProperty(JsonField.VALUE)
  private Object value;

  @JsonProperty(JsonField.FROM)
  private String from;

  @JsonProperty(JsonField.OP)
  public JSONPatchAction.OpType getOp() {
    return this.op;
  }

  @JsonProperty(JsonField.OP)
  public void setOp(JSONPatchAction.OpType op) {
    this.op = op;
  }

  @JsonProperty(JsonField.PATH)
  public String getPath() {
    return this.path;
  }

  @JsonProperty(JsonField.PATH)
  public void setPath(String path) {
    this.path = path;
  }

  @JsonProperty(JsonField.VALUE)
  public Object getValue() {
    return this.value;
  }

  @JsonProperty(JsonField.VALUE)
  public void setValue(Object value) {
    this.value = value;
  }

  @JsonProperty(JsonField.FROM)
  public String getFrom() {
    return this.from;
  }

  @JsonProperty(JsonField.FROM)
  public void setFrom(String from) {
    this.from = from;
  }
}
