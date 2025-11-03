package vdi.service.rest.generated.model;

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

  @JsonProperty(JsonField.TYPE)
  DatasetTypeOutput getType();

  @JsonProperty(JsonField.TYPE)
  void setType(DatasetTypeOutput type);

  @JsonProperty(JsonField.INSTALL_TARGETS)
  List<String> getInstallTargets();

  @JsonProperty(JsonField.INSTALL_TARGETS)
  void setInstallTargets(List<String> installTargets);
}
