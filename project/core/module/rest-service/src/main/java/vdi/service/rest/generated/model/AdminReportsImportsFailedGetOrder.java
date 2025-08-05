package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AdminReportsImportsFailedGetOrder {
  @JsonProperty("asc")
  ASC("asc"),

  @JsonProperty("desc")
  DESC("desc");

  private String name;

  AdminReportsImportsFailedGetOrder(String name) {
    this.name = name;
  }
}
