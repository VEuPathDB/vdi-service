package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetsVdiIdFilesFileNameFileName {
  @JsonProperty("metadata.json")
  METADATAJSON("metadata.json"),

  @JsonProperty("upload-errors.json")
  UPLOADERRORSJSON("upload-errors.json");

  public final String value;

  public String getValue() {
    return this.value;
  }

  DatasetsVdiIdFilesFileNameFileName(String name) {
    this.value = name;
  }
}
