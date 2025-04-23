package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShareOfferStatus {
  @JsonProperty("open")
  OPEN("open"),

  @JsonProperty("accepted")
  ACCEPTED("accepted"),

  @JsonProperty("rejected")
  REJECTED("rejected");

  public final String value;

  public String getValue() {
    return this.value;
  }

  ShareOfferStatus(String name) {
    this.value = name;
  }
}
