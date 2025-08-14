package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PatchAction {
  @JsonProperty("remove")
  REMOVE("remove"),

  @JsonProperty("replace")
  REPLACE("replace");

  public final String value;

  public String getValue() {
    return this.value;
  }

  PatchAction(String name) {
    this.value = name;
  }
}
