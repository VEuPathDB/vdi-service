package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "projectId",
    "metaStatus",
    "metaMessage",
    "dataStatus",
    "dataMessage"
})
public class DatasetInstallStatusEntryImpl implements DatasetInstallStatusEntry {
  @JsonProperty("projectId")
  private String projectId;

  @JsonProperty("metaStatus")
  private DatasetInstallStatus metaStatus;

  @JsonProperty("metaMessage")
  private String metaMessage;

  @JsonProperty("dataStatus")
  private DatasetInstallStatus dataStatus;

  @JsonProperty("dataMessage")
  private String dataMessage;

  @JsonProperty("projectId")
  public String getProjectId() {
    return this.projectId;
  }

  @JsonProperty("projectId")
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  @JsonProperty("metaStatus")
  public DatasetInstallStatus getMetaStatus() {
    return this.metaStatus;
  }

  @JsonProperty("metaStatus")
  public void setMetaStatus(DatasetInstallStatus metaStatus) {
    this.metaStatus = metaStatus;
  }

  @JsonProperty("metaMessage")
  public String getMetaMessage() {
    return this.metaMessage;
  }

  @JsonProperty("metaMessage")
  public void setMetaMessage(String metaMessage) {
    this.metaMessage = metaMessage;
  }

  @JsonProperty("dataStatus")
  public DatasetInstallStatus getDataStatus() {
    return this.dataStatus;
  }

  @JsonProperty("dataStatus")
  public void setDataStatus(DatasetInstallStatus dataStatus) {
    this.dataStatus = dataStatus;
  }

  @JsonProperty("dataMessage")
  public String getDataMessage() {
    return this.dataMessage;
  }

  @JsonProperty("dataMessage")
  public void setDataMessage(String dataMessage) {
    this.dataMessage = dataMessage;
  }
}
