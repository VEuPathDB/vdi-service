package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetId",
    "owner",
    "shareStatus",
    "datasetType",
    "projectIds"
})
public class ShareOfferEntryImpl implements ShareOfferEntry {
  @JsonProperty(JsonField.DATASET_ID)
  private String datasetId;

  @JsonProperty(JsonField.OWNER)
  private DatasetOwner owner;

  @JsonProperty(JsonField.SHARE_STATUS)
  private ShareOfferStatus shareStatus;

  @JsonProperty(JsonField.DATASET_TYPE)
  private DatasetTypeOutput datasetType;

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

  @JsonProperty(JsonField.OWNER)
  public DatasetOwner getOwner() {
    return this.owner;
  }

  @JsonProperty(JsonField.OWNER)
  public void setOwner(DatasetOwner owner) {
    this.owner = owner;
  }

  @JsonProperty(JsonField.SHARE_STATUS)
  public ShareOfferStatus getShareStatus() {
    return this.shareStatus;
  }

  @JsonProperty(JsonField.SHARE_STATUS)
  public void setShareStatus(ShareOfferStatus shareStatus) {
    this.shareStatus = shareStatus;
  }

  @JsonProperty(JsonField.DATASET_TYPE)
  public DatasetTypeOutput getDatasetType() {
    return this.datasetType;
  }

  @JsonProperty(JsonField.DATASET_TYPE)
  public void setDatasetType(DatasetTypeOutput datasetType) {
    this.datasetType = datasetType;
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
