package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetInstallStatus {
  @JsonProperty("running")
  RUNNING("running"),

  @JsonProperty("complete")
  COMPLETE("complete"),

  @JsonProperty("failed-validation")
  FAILEDVALIDATION("failed-validation"),

  @JsonProperty("failed-installation")
  FAILEDINSTALLATION("failed-installation"),

  @JsonProperty("ready-for-reinstall")
  READYFORREINSTALL("ready-for-reinstall"),

  @JsonProperty("missing-dependency")
  MISSINGDEPENDENCY("missing-dependency");

  private String name;

  DatasetInstallStatus(String name) {
    this.name = name;
  }
}
