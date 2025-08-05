package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetId",
    "owner",
    "datasetType",
    "installTargets",
    "messages"
})
public class BrokenImportDetailsImpl implements BrokenImportDetails {
  @JsonProperty(JsonField.DATASET_ID)
  private String datasetId;

  @JsonProperty(JsonField.OWNER)
  private long owner;

  @JsonProperty(JsonField.DATASET_TYPE)
  private DatasetTypeOutput datasetType;

  @JsonProperty(JsonField.INSTALL_TARGETS)
  private List<String> installTargets;

  @JsonProperty(JsonField.MESSAGES)
  private List<String> messages;

  @JsonProperty(JsonField.DATASET_ID)
  public String getDatasetId() {
    return this.datasetId;
  }

  @JsonProperty(JsonField.DATASET_ID)
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  @JsonProperty(JsonField.OWNER)
  public long getOwner() {
    return this.owner;
  }

  @JsonProperty(JsonField.OWNER)
  public void setOwner(long owner) {
    this.owner = owner;
  }

  @JsonProperty(JsonField.DATASET_TYPE)
  public DatasetTypeOutput getDatasetType() {
    return this.datasetType;
  }

  @JsonProperty(JsonField.DATASET_TYPE)
  public void setDatasetType(DatasetTypeOutput datasetType) {
    this.datasetType = datasetType;
  }

  @JsonProperty(JsonField.INSTALL_TARGETS)
  public List<String> getInstallTargets() {
    return this.installTargets;
  }

  @JsonProperty(JsonField.INSTALL_TARGETS)
  public void setInstallTargets(List<String> installTargets) {
    this.installTargets = installTargets;
  }

  @JsonProperty(JsonField.MESSAGES)
  public List<String> getMessages() {
    return this.messages;
  }

  @JsonProperty(JsonField.MESSAGES)
  public void setMessages(List<String> messages) {
    this.messages = messages;
  }
}
