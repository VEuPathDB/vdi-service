package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("datasetID")
public class DatasetPostResponseImpl implements DatasetPostResponse {
  @JsonProperty("datasetID")
  private String datasetID;

  @JsonProperty("datasetID")
  public String getDatasetID() {
    return this.datasetID;
  }

  @JsonProperty("datasetID")
  public void setDatasetID(String datasetID) {
    this.datasetID = datasetID;
  }
}
