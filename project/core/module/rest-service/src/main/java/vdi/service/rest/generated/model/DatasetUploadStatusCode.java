package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetUploadStatusCode {
  @JsonProperty("running")
  RUNNING("running"),

  @JsonProperty("complete")
  COMPLETE("complete"),

  @JsonProperty("failed")
  FAILED("failed");

  public final String value;

  public String getValue() {
    return this.value;
  }

  DatasetUploadStatusCode(String name) {
    this.value = name;
  }
}
