package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = JSONPatchActionImpl.class
)
public interface JSONPatchAction {
  @JsonProperty(JsonField.OP)
  OpType getOp();

  @JsonProperty(JsonField.OP)
  void setOp(OpType op);

  @JsonProperty(JsonField.PATH)
  String getPath();

  @JsonProperty(JsonField.PATH)
  void setPath(String path);

  @JsonProperty(JsonField.VALUE)
  Object getValue();

  @JsonProperty(JsonField.VALUE)
  void setValue(Object value);

  @JsonProperty(JsonField.FROM)
  String getFrom();

  @JsonProperty(JsonField.FROM)
  void setFrom(String from);

  enum OpType {
    @JsonProperty("add")
    ADD("add"),

    @JsonProperty("remove")
    REMOVE("remove"),

    @JsonProperty("replace")
    REPLACE("replace"),

    @JsonProperty("move")
    MOVE("move"),

    @JsonProperty("copy")
    COPY("copy"),

    @JsonProperty("test")
    TEST("test");

    public final String value;

    public String getValue() {
      return this.value;
    }

    OpType(String name) {
      this.value = name;
    }
  }
}
