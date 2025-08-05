package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "userId",
    "datasetId"
})
public class DatasetObjectPurgeRequestBodyImpl implements DatasetObjectPurgeRequestBody {
  @JsonProperty(JsonField.USER_ID)
  private long userId;

  @JsonProperty(JsonField.DATASET_ID)
  private String datasetId;

  @JsonProperty(JsonField.USER_ID)
  public long getUserId() {
    return this.userId;
  }

  @JsonProperty(JsonField.USER_ID)
  public void setUserId(long userId) {
    this.userId = userId;
  }

  @JsonProperty(JsonField.DATASET_ID)
  public String getDatasetId() {
    return this.datasetId;
  }

  @JsonProperty(JsonField.DATASET_ID)
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }
}
