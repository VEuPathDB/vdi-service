package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AdminReportsImportsFailedGetOrder {
  @JsonProperty("asc")
  ASC("asc"),

  @JsonProperty("desc")
  DESC("desc");

  public final String value;

  public String getValue() {
    return this.value;
  }

  AdminReportsImportsFailedGetOrder(String name) {
    this.value = name;
  }
}
