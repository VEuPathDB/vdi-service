package org.veupathdb.service.vdi.generated.model;

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
  @JsonProperty("datasetId")
  private String datasetId;

  @JsonProperty("owner")
  private DatasetOwner owner;

  @JsonProperty("shareStatus")
  private ShareOfferStatus shareStatus;

  @JsonProperty("datasetType")
  private DatasetTypeInfo datasetType;

  @JsonProperty("projectIds")
  private List<String> projectIds;

  @JsonProperty("datasetId")
  public String getDatasetId() {
    return this.datasetId;
  }

  @JsonProperty("datasetId")
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  @JsonProperty("owner")
  public DatasetOwner getOwner() {
    return this.owner;
  }

  @JsonProperty("owner")
  public void setOwner(DatasetOwner owner) {
    this.owner = owner;
  }

  @JsonProperty("shareStatus")
  public ShareOfferStatus getShareStatus() {
    return this.shareStatus;
  }

  @JsonProperty("shareStatus")
  public void setShareStatus(ShareOfferStatus shareStatus) {
    this.shareStatus = shareStatus;
  }

  @JsonProperty("datasetType")
  public DatasetTypeInfo getDatasetType() {
    return this.datasetType;
  }

  @JsonProperty("datasetType")
  public void setDatasetType(DatasetTypeInfo datasetType) {
    this.datasetType = datasetType;
  }

  @JsonProperty("projectIds")
  public List<String> getProjectIds() {
    return this.projectIds;
  }

  @JsonProperty("projectIds")
  public void setProjectIds(List<String> projectIds) {
    this.projectIds = projectIds;
  }
}
