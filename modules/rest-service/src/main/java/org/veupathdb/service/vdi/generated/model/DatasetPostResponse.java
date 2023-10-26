package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetPostResponseImpl.class
)
public interface DatasetPostResponse {
  @JsonProperty("datasetId")
  String getDatasetId();

  @JsonProperty("datasetId")
  void setDatasetId(String datasetId);
}
