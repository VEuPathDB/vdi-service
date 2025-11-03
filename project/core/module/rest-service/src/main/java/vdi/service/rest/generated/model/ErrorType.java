package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ErrorType {
  @JsonProperty("bad-request")
  BADREQUEST("bad-request"),

  @JsonProperty("unauthorized")
  UNAUTHORIZED("unauthorized"),

  @JsonProperty("forbidden")
  FORBIDDEN("forbidden"),

  @JsonProperty("not-found")
  NOTFOUND("not-found"),

  @JsonProperty("bad-method")
  BADMETHOD("bad-method"),

  @JsonProperty("conflict")
  CONFLICT("conflict"),

  @JsonProperty("gone")
  GONE("gone"),

  @JsonProperty("invalid-input")
  INVALIDINPUT("invalid-input"),

  @JsonProperty("failed-dependency")
  FAILEDDEPENDENCY("failed-dependency"),

  @JsonProperty("too-early")
  TOOEARLY("too-early"),

  @JsonProperty("server-error")
  SERVERERROR("server-error");

  public final String value;

  public String getValue() {
    return this.value;
  }

  ErrorType(String name) {
    this.value = name;
  }
}
