package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShareOfferAction {
  @JsonProperty("grant")
  GRANT("grant"),

  @JsonProperty("revoke")
  REVOKE("revoke");

  public final String value;

  public String getValue() {
    return this.value;
  }

  ShareOfferAction(String name) {
    this.value = name;
  }
}
