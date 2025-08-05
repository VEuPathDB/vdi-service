package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShareReceiptAction {
  @JsonProperty("accept")
  ACCEPT("accept"),

  @JsonProperty("reject")
  REJECT("reject");

  private String name;

  ShareReceiptAction(String name) {
    this.name = name;
  }
}
