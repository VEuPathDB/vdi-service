package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UsersSelfShareOffersGetStatus {
  @JsonProperty("open")
  OPEN("open"),

  @JsonProperty("accepted")
  ACCEPTED("accepted"),

  @JsonProperty("rejected")
  REJECTED("rejected"),

  @JsonProperty("all")
  ALL("all");

  public final String value;

  public String getValue() {
    return this.value;
  }

  UsersSelfShareOffersGetStatus(String name) {
    this.value = name;
  }
}
