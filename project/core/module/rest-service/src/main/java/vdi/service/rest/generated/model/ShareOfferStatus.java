package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShareOfferStatus {
  @JsonProperty("open")
  OPEN("open"),

  @JsonProperty("accepted")
  ACCEPTED("accepted"),

  @JsonProperty("rejected")
  REJECTED("rejected");

  private String name;

  ShareOfferStatus(String name) {
    this.name = name;
  }
}
