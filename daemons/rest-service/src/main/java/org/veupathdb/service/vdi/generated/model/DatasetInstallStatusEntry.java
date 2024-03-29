package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetInstallStatusEntryImpl.class
)
public interface DatasetInstallStatusEntry {
  @JsonProperty("projectId")
  String getProjectId();

  @JsonProperty("projectId")
  void setProjectId(String projectId);

  @JsonProperty("metaStatus")
  DatasetInstallStatus getMetaStatus();

  @JsonProperty("metaStatus")
  void setMetaStatus(DatasetInstallStatus metaStatus);

  @JsonProperty("metaMessage")
  String getMetaMessage();

  @JsonProperty("metaMessage")
  void setMetaMessage(String metaMessage);

  @JsonProperty("dataStatus")
  DatasetInstallStatus getDataStatus();

  @JsonProperty("dataStatus")
  void setDataStatus(DatasetInstallStatus dataStatus);

  @JsonProperty("dataMessage")
  String getDataMessage();

  @JsonProperty("dataMessage")
  void setDataMessage(String dataMessage);
}
