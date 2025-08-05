package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetRevisionAction {
  @JsonProperty("revise")
  REVISE("revise");

  private String name;

  DatasetRevisionAction(String name) {
    this.name = name;
  }
}
