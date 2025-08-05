package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetVisibility {
  @JsonProperty("private")
  PRIVATE("private"),

  @JsonProperty("protected")
  PROTECTED("protected"),

  @JsonProperty("public")
  PUBLIC("public");

  private String name;

  DatasetVisibility(String name) {
    this.name = name;
  }
}
