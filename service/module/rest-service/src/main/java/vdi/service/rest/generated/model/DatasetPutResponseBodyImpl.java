package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("datasetId")
public class DatasetPutResponseBodyImpl implements DatasetPutResponseBody {
  @JsonProperty(JsonField.DATASET_ID)
  private String datasetId;

  @JsonProperty(JsonField.DATASET_ID)
  public String getDatasetId() {
    return this.datasetId;
  }

  @JsonProperty(JsonField.DATASET_ID)
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }
}
