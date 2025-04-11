package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetId",
    "projectIds"
})
public class InstallCleanupTargetImpl implements InstallCleanupTarget {
  @JsonProperty(JsonField.DATASET_ID)
  private String datasetId;

  @JsonProperty(JsonField.PROJECT_IDS)
  private List<String> projectIds;

  @JsonProperty(JsonField.DATASET_ID)
  public String getDatasetId() {
    return this.datasetId;
  }

  @JsonProperty(JsonField.DATASET_ID)
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  @JsonProperty(JsonField.PROJECT_IDS)
  public List<String> getProjectIds() {
    return this.projectIds;
  }

  @JsonProperty(JsonField.PROJECT_IDS)
  public void setProjectIds(List<String> projectIds) {
    this.projectIds = projectIds;
  }
}
