package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "projectID",
    "status"
})
public class DatasetInstallStatusEntryImpl implements DatasetInstallStatusEntry {
  @JsonProperty("projectID")
  private String projectID;

  @JsonProperty("status")
  private DatasetInstallStatus status;

  @JsonProperty("projectID")
  public String getProjectID() {
    return this.projectID;
  }

  @JsonProperty("projectID")
  public void setProjectID(String projectID) {
    this.projectID = projectID;
  }

  @JsonProperty("status")
  public DatasetInstallStatus getStatus() {
    return this.status;
  }

  @JsonProperty("status")
  public void setStatus(DatasetInstallStatus status) {
    this.status = status;
  }
}
