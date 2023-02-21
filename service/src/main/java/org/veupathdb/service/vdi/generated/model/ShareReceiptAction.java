package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShareReceiptAction {
  @JsonProperty("accept")
  ACCEPT("accept"),

  @JsonProperty("reject")
  REJECT("reject");

  public final String name;

  ShareReceiptAction(String name) {
    this.name = name;
  }


  public String getName() {
    return this.name;
  }
}
