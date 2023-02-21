package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = ShareOfferEntryImpl.class
)
public interface ShareOfferEntry {
  @JsonProperty("datasetID")
  String getDatasetID();

  @JsonProperty("datasetID")
  void setDatasetID(String datasetID);

  @JsonProperty("owner")
  DatasetOwner getOwner();

  @JsonProperty("owner")
  void setOwner(DatasetOwner owner);

  @JsonProperty("shareStatus")
  ShareOfferStatus getShareStatus();

  @JsonProperty("shareStatus")
  void setShareStatus(ShareOfferStatus shareStatus);

  @JsonProperty("datasetType")
  DatasetTypeInfo getDatasetType();

  @JsonProperty("datasetType")
  void setDatasetType(DatasetTypeInfo datasetType);

  @JsonProperty("projectIDs")
  List<String> getProjectIDs();

  @JsonProperty("projectIDs")
  void setProjectIDs(List<String> projectIDs);
}
