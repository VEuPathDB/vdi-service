package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = LinkedDatasetImpl.class
)
public interface LinkedDataset {
  @JsonProperty(JsonField.DATASET_URI)
  String getDatasetUri();

  @JsonProperty(JsonField.DATASET_URI)
  void setDatasetUri(String datasetUri);

  @JsonProperty(JsonField.SHARES_RECORDS)
  Boolean getSharesRecords();

  @JsonProperty(JsonField.SHARES_RECORDS)
  void setSharesRecords(Boolean sharesRecords);
}
