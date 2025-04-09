package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = ShareOfferEntryImpl.class
)
public interface ShareOfferEntry {
  @JsonProperty(JsonField.DATASET_ID)
  String getDatasetId();

  @JsonProperty(JsonField.DATASET_ID)
  void setDatasetId(String datasetId);

  @JsonProperty(JsonField.OWNER)
  DatasetOwner getOwner();

  @JsonProperty(JsonField.OWNER)
  void setOwner(DatasetOwner owner);

  @JsonProperty(JsonField.SHARE_STATUS)
  ShareOfferStatus getShareStatus();

  @JsonProperty(JsonField.SHARE_STATUS)
  void setShareStatus(ShareOfferStatus shareStatus);

  @JsonProperty(JsonField.DATASET_TYPE)
  DatasetTypeResponseBody getDatasetType();

  @JsonProperty(JsonField.DATASET_TYPE)
  void setDatasetType(DatasetTypeResponseBody datasetType);

  @JsonProperty(JsonField.PROJECT_IDS)
  List<String> getProjectIds();

  @JsonProperty(JsonField.PROJECT_IDS)
  void setProjectIds(List<String> projectIds);
}
