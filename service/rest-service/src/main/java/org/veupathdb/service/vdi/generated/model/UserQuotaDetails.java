package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = UserQuotaDetailsImpl.class
)
public interface UserQuotaDetails {
  @JsonProperty("limit")
  Long getLimit();

  @JsonProperty("limit")
  void setLimit(Long limit);

  @JsonProperty("usage")
  Long getUsage();

  @JsonProperty("usage")
  void setUsage(Long usage);
}
