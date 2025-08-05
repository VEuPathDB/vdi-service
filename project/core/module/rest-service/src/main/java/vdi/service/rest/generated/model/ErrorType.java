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

  @JsonProperty("server-error")
  SERVERERROR("server-error");

  private String name;

  ErrorType(String name) {
    this.name = name;
  }
}
