package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetInstallStatus {
  @JsonProperty("running")
  RUNNING("running"),

  @JsonProperty("complete")
  COMPLETE("complete"),

  @JsonProperty("invalid")
  INVALID("invalid"),

  @JsonProperty("failed")
  FAILED("failed"),

  @JsonProperty("ready-for-reinstall")
  READYFORREINSTALL("ready-for-reinstall");

  private final String value;

  public String getValue() {
    return this.value;
  }

  DatasetInstallStatus(String name) {
    this.value = name;
  }
}
