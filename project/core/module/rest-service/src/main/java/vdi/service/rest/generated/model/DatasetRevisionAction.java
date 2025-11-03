package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetRevisionAction {
  @JsonProperty("create")
  CREATE("create"),

  @JsonProperty("extend")
  EXTEND("extend"),

  @JsonProperty("revise")
  REVISE("revise");

  public final String value;

  public String getValue() {
    return this.value;
  }

  DatasetRevisionAction(String name) {
    this.value = name;
  }
}
