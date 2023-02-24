package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DeleteCleanupRequestImpl.class
)
public interface DeleteCleanupRequest {
  @JsonProperty("authorization")
  String getAuthorization();

  @JsonProperty("authorization")
  void setAuthorization(String authorization);
}
