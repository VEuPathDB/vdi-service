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

  public final String name;

  DatasetInstallStatus(String name) {
    this.name = name;
  }


  public String getName() {
    return this.name;
  }
}
