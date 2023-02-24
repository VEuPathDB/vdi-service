package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShareReceiptAction {
  @JsonProperty("accept")
  ACCEPT("accept"),

  @JsonProperty("reject")
  REJECT("reject");

  private final String value;

  public String getValue() {
    return this.value;
  }

  ShareReceiptAction(String name) {
    this.value = name;
  }
}
