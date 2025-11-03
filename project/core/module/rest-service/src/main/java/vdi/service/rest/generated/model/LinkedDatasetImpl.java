package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetUri",
    "sharesRecords"
})
public class LinkedDatasetImpl implements LinkedDataset {
  @JsonProperty(JsonField.DATASET_URI)
  private String datasetUri;

  @JsonProperty(JsonField.SHARES_RECORDS)
  private Boolean sharesRecords;

  @JsonProperty(JsonField.DATASET_URI)
  public String getDatasetUri() {
    return this.datasetUri;
  }

  @JsonProperty(JsonField.DATASET_URI)
  public void setDatasetUri(String datasetUri) {
    this.datasetUri = datasetUri;
  }

  @JsonProperty(JsonField.SHARES_RECORDS)
  public Boolean getSharesRecords() {
    return this.sharesRecords;
  }

  @JsonProperty(JsonField.SHARES_RECORDS)
  public void setSharesRecords(Boolean sharesRecords) {
    this.sharesRecords = sharesRecords;
  }
}
