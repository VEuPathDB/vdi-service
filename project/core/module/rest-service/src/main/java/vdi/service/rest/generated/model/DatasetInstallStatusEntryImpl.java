package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "installTarget",
    "metaStatus",
    "metaMessages",
    "dataStatus",
    "dataMessages"
})
public class DatasetInstallStatusEntryImpl implements DatasetInstallStatusEntry {
  @JsonProperty(JsonField.INSTALL_TARGET)
  private String installTarget;

  @JsonProperty(JsonField.META_STATUS)
  private DatasetInstallStatus metaStatus;

  @JsonProperty(JsonField.META_MESSAGES)
  private List<String> metaMessages;

  @JsonProperty(JsonField.DATA_STATUS)
  private DatasetInstallStatus dataStatus;

  @JsonProperty(JsonField.DATA_MESSAGES)
  private List<String> dataMessages;

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

  @JsonProperty(JsonField.META_MESSAGES)
  public List<String> getMetaMessages() {
    return this.metaMessages;
  }

  @JsonProperty(JsonField.META_MESSAGES)
  public void setMetaMessages(List<String> metaMessages) {
    this.metaMessages = metaMessages;
  }

  @JsonProperty(JsonField.DATA_STATUS)
  public DatasetInstallStatus getDataStatus() {
    return this.dataStatus;
  }

  @JsonProperty(JsonField.DATA_STATUS)
  public void setDataStatus(DatasetInstallStatus dataStatus) {
    this.dataStatus = dataStatus;
  }

  @JsonProperty(JsonField.DATA_MESSAGES)
  public List<String> getDataMessages() {
    return this.dataMessages;
  }

  @JsonProperty(JsonField.DATA_MESSAGES)
  public void setDataMessages(List<String> dataMessages) {
    this.dataMessages = dataMessages;
  }
}
