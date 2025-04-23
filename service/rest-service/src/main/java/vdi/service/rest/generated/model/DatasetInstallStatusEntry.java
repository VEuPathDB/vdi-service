package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetInstallStatusEntryImpl.class
)
public interface DatasetInstallStatusEntry {
  @JsonProperty(JsonField.PROJECT_ID)
  String getProjectId();

  @JsonProperty(JsonField.PROJECT_ID)
  void setProjectId(String projectId);

  @JsonProperty(JsonField.META_STATUS)
  DatasetInstallStatus getMetaStatus();

  @JsonProperty(JsonField.META_STATUS)
  void setMetaStatus(DatasetInstallStatus metaStatus);

  @JsonProperty(JsonField.META_MESSAGE)
  String getMetaMessage();

  @JsonProperty(JsonField.META_MESSAGE)
  void setMetaMessage(String metaMessage);

  @JsonProperty(JsonField.DATA_STATUS)
  DatasetInstallStatus getDataStatus();

  @JsonProperty(JsonField.DATA_STATUS)
  void setDataStatus(DatasetInstallStatus dataStatus);

  @JsonProperty(JsonField.DATA_MESSAGE)
  String getDataMessage();

  @JsonProperty(JsonField.DATA_MESSAGE)
  void setDataMessage(String dataMessage);
}
