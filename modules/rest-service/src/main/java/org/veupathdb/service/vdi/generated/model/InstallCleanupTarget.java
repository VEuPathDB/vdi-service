package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = InstallCleanupTargetImpl.class
)
public interface InstallCleanupTarget {
  @JsonProperty("datasetID")
  String getDatasetID();

  @JsonProperty("datasetID")
  void setDatasetID(String datasetID);

  @JsonProperty("projectID")
  String getProjectID();

  @JsonProperty("projectID")
  void setProjectID(String projectID);
}
