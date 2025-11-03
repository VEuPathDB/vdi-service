package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AdminReportsImportsFailedGetSort {
  @JsonProperty("date")
  DATE("date");

  public final String value;

  public String getValue() {
    return this.value;
  }

  AdminReportsImportsFailedGetSort(String name) {
    this.value = name;
  }
}
