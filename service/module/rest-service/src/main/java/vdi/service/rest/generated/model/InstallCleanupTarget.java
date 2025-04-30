package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = InstallCleanupTargetImpl.class
)
public interface InstallCleanupTarget {
  @JsonProperty(JsonField.DATASET_ID)
  String getDatasetId();

  @JsonProperty(JsonField.DATASET_ID)
  void setDatasetId(String datasetId);

  @JsonProperty(JsonField.PROJECT_ID)
  String getProjectId();

  @JsonProperty(JsonField.PROJECT_ID)
  void setProjectId(String projectId);
}
