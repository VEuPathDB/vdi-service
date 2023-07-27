package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetImportStatus {
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

  private String name;

  DatasetImportStatus(String name) {
    this.name = name;
  }
}
