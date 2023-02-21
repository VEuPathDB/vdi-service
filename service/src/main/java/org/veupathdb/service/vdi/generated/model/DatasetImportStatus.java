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

  public final String name;

  DatasetImportStatus(String name) {
    this.name = name;
  }


  public String getName() {
    return this.name;
  }
}
