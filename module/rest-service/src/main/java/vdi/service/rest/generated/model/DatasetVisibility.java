package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetVisibility {
  @JsonProperty("private")
  PRIVATE("private"),

  @JsonProperty("protected")
  PROTECTED("protected"),

  @JsonProperty("public")
  PUBLIC("public");

  public final String value;

  public String getValue() {
    return this.value;
  }

  DatasetVisibility(String name) {
    this.value = name;
  }
}
