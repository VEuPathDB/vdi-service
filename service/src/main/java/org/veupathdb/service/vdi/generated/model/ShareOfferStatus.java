package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShareOfferStatus {
  @JsonProperty("open")
  OPEN("open"),

  @JsonProperty("accepted")
  ACCEPTED("accepted"),

  @JsonProperty("rejected")
  REJECTED("rejected");

  public final String name;

  ShareOfferStatus(String name) {
    this.name = name;
  }


  public String getName() {
    return this.name;
  }
}
