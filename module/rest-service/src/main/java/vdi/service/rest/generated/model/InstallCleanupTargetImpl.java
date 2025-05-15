package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetId",
    "projectId"
})
public class InstallCleanupTargetImpl implements InstallCleanupTarget {
  @JsonProperty(JsonField.DATASET_ID)
  private String datasetId;

  @JsonProperty(JsonField.PROJECT_ID)
  private String projectId;

  @JsonProperty(JsonField.DATASET_ID)
  public String getDatasetId() {
    return this.datasetId;
  }

  @JsonProperty(JsonField.DATASET_ID)
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  @JsonProperty(JsonField.PROJECT_ID)
  public String getProjectId() {
    return this.projectId;
  }

  @JsonProperty(JsonField.PROJECT_ID)
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }
}
