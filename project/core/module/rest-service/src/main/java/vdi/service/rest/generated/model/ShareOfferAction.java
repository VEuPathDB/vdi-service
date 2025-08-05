package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShareOfferAction {
  @JsonProperty("grant")
  GRANT("grant"),

  @JsonProperty("revoke")
  REVOKE("revoke");

  private String name;

  ShareOfferAction(String name) {
    this.name = name;
  }
}
