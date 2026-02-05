package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetImportStatusCode {
  @JsonProperty("queued")
  QUEUED("queued"),

  @JsonProperty("in-progress")
  INPROGRESS("in-progress"),

  @JsonProperty("complete")
  COMPLETE("complete"),

  @JsonProperty("invalid")
  INVALID("invalid"),

  @JsonProperty("failed")
  FAILED("failed");

  public final String value;

  public String getValue() {
    return this.value;
  }

  DatasetImportStatusCode(String name) {
    this.value = name;
  }
}
