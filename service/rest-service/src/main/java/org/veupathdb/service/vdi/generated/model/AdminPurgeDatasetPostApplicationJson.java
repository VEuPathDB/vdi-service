package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = AdminPurgeDatasetPostApplicationJsonImpl.class
)
public interface AdminPurgeDatasetPostApplicationJson {
  @JsonProperty(JsonField.USER_ID)
  Long getUserId();

  @JsonProperty(JsonField.USER_ID)
  void setUserId(Long userId);

  @JsonProperty(JsonField.DATASET_ID)
  String getDatasetId();

  @JsonProperty(JsonField.DATASET_ID)
  void setDatasetId(String datasetId);
}
