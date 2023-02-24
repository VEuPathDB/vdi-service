package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetID",
    "projectID"
})
public class InstallCleanupTargetImpl implements InstallCleanupTarget {
  @JsonProperty("datasetID")
  private String datasetID;

  @JsonProperty("projectID")
  private String projectID;

  @JsonProperty("datasetID")
  public String getDatasetID() {
    return this.datasetID;
  }

  @JsonProperty("datasetID")
  public void setDatasetID(String datasetID) {
    this.datasetID = datasetID;
  }

  @JsonProperty("projectID")
  public String getProjectID() {
    return this.projectID;
  }

  @JsonProperty("projectID")
  public void setProjectID(String projectID) {
    this.projectID = projectID;
  }
}
