package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = InstallCleanupTargetImpl.class
)
public interface InstallCleanupTarget {
  @JsonProperty("datasetId")
  String getDatasetId();

  @JsonProperty("datasetId")
  void setDatasetId(String datasetId);

  @JsonProperty("projectId")
  String getProjectId();

  @JsonProperty("projectId")
  void setProjectId(String projectId);
}
