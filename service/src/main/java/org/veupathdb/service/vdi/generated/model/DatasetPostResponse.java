package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetPostResponseImpl.class
)
public interface DatasetPostResponse {
  @JsonProperty("datasetID")
  String getDatasetID();

  @JsonProperty("datasetID")
  void setDatasetID(String datasetID);
}
