package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShareOfferAction {
  @JsonProperty("grant")
  GRANT("grant"),

  @JsonProperty("revoke")
  REVOKE("revoke");

  public final String name;

  ShareOfferAction(String name) {
    this.name = name;
  }


  public String getName() {
    return this.name;
  }
}
