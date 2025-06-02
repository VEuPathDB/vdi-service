package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "installTarget",
    "metaStatus",
    "metaMessage",
    "dataStatus",
    "dataMessage"
})
public class DatasetInstallStatusEntryImpl implements DatasetInstallStatusEntry {
  @JsonProperty(JsonField.INSTALL_TARGET)
  private String installTarget;

  @JsonProperty(JsonField.META_STATUS)
  private DatasetInstallStatus metaStatus;

  @JsonProperty(JsonField.META_MESSAGE)
  private String metaMessage;

  @JsonProperty(JsonField.DATA_STATUS)
  private DatasetInstallStatus dataStatus;

  @JsonProperty(JsonField.DATA_MESSAGE)
  private String dataMessage;

  @JsonProperty(JsonField.INSTALL_TARGET)
  public String getInstallTarget() {
    return this.installTarget;
  }

  @JsonProperty(JsonField.INSTALL_TARGET)
  public void setInstallTarget(String installTarget) {
    this.installTarget = installTarget;
  }

  @JsonProperty(JsonField.META_STATUS)
  public DatasetInstallStatus getMetaStatus() {
    return this.metaStatus;
  }

  @JsonProperty(JsonField.META_STATUS)
  public void setMetaStatus(DatasetInstallStatus metaStatus) {
    this.metaStatus = metaStatus;
  }

  @JsonProperty(JsonField.META_MESSAGE)
  public String getMetaMessage() {
    return this.metaMessage;
  }

  @JsonProperty(JsonField.META_MESSAGE)
  public void setMetaMessage(String metaMessage) {
    this.metaMessage = metaMessage;
  }

  @JsonProperty(JsonField.DATA_STATUS)
  public DatasetInstallStatus getDataStatus() {
    return this.dataStatus;
  }

  @JsonProperty(JsonField.DATA_STATUS)
  public void setDataStatus(DatasetInstallStatus dataStatus) {
    this.dataStatus = dataStatus;
  }

  @JsonProperty(JsonField.DATA_MESSAGE)
  public String getDataMessage() {
    return this.dataMessage;
  }

  @JsonProperty(JsonField.DATA_MESSAGE)
  public void setDataMessage(String dataMessage) {
    this.dataMessage = dataMessage;
  }
}
