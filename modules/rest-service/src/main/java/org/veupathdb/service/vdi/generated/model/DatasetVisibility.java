package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetVisibility {
  @JsonProperty("private")
  PRIVATE("private"),

  @JsonProperty("protected")
  PROTECTED("protected"),

  @JsonProperty("public")
  PUBLIC("public");

  private final String value;

  public String getValue() {
    return this.value;
  }

  DatasetVisibility(String name) {
    this.value = name;
  }
}
