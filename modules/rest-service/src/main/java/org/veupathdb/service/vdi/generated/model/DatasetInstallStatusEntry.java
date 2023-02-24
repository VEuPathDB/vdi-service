package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetInstallStatusEntryImpl.class
)
public interface DatasetInstallStatusEntry {
  @JsonProperty("projectID")
  String getProjectID();

  @JsonProperty("projectID")
  void setProjectID(String projectID);

  @JsonProperty("status")
  DatasetInstallStatus getStatus();

  @JsonProperty("status")
  void setStatus(DatasetInstallStatus status);
}
