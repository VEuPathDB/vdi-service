package vdi.service.rest.generated.model;

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

  public final String value;

  public String getValue() {
    return this.value;
  }

  DatasetInstallStatus(String name) {
    this.value = name;
  }
}
