package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetImportStatus {
  @JsonProperty("awaiting-import")
  AWAITINGIMPORT("awaiting-import"),

  @JsonProperty("importing")
  IMPORTING("importing"),

  @JsonProperty("imported")
  IMPORTED("imported"),

  @JsonProperty("import-failed")
  IMPORTFAILED("import-failed");

  private final String value;

  public String getValue() {
    return this.value;
  }

  DatasetImportStatus(String name) {
    this.value = name;
  }
}
