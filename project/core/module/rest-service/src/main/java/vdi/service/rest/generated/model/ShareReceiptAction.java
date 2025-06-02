package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShareReceiptAction {
  @JsonProperty("accept")
  ACCEPT("accept"),

  @JsonProperty("reject")
  REJECT("reject");

  public final String value;

  public String getValue() {
    return this.value;
  }

  ShareReceiptAction(String name) {
    this.value = name;
  }
}
