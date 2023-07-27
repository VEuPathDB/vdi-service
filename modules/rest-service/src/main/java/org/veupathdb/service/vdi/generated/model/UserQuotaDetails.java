package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = UserQuotaDetailsImpl.class
)
public interface UserQuotaDetails {
  @JsonProperty("limit")
  long getLimit();

  @JsonProperty("limit")
  void setLimit(long limit);

  @JsonProperty("usage")
  long getUsage();

  @JsonProperty("usage")
  void setUsage(long usage);
}
