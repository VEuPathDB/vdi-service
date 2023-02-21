package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetID",
    "owner",
    "shareStatus",
    "datasetType",
    "projectIDs"
})
public class ShareOfferEntryImpl implements ShareOfferEntry {
  @JsonProperty("datasetID")
  private String datasetID;

  @JsonProperty("owner")
  private DatasetOwner owner;

  @JsonProperty("shareStatus")
  private ShareOfferStatus shareStatus;

  @JsonProperty("datasetType")
  private DatasetTypeInfo datasetType;

  @JsonProperty("projectIDs")
  private List<String> projectIDs;

  @JsonProperty("datasetID")
  public String getDatasetID() {
    return this.datasetID;
  }

  @JsonProperty("datasetID")
  public void setDatasetID(String datasetID) {
    this.datasetID = datasetID;
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

  @JsonProperty("projectIDs")
  public List<String> getProjectIDs() {
    return this.projectIDs;
  }

  @JsonProperty("projectIDs")
  public void setProjectIDs(List<String> projectIDs) {
    this.projectIDs = projectIDs;
  }
}
