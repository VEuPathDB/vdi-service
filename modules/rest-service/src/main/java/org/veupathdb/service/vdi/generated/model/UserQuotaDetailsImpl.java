package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "limit",
    "usage"
})
public class UserQuotaDetailsImpl implements UserQuotaDetails {
  @JsonProperty("limit")
  private long limit;

  @JsonProperty("usage")
  private long usage;

  @JsonProperty("limit")
  public long getLimit() {
    return this.limit;
  }

  @JsonProperty("limit")
  public void setLimit(long limit) {
    this.limit = limit;
  }

  @JsonProperty("usage")
  public long getUsage() {
    return this.usage;
  }

  @JsonProperty("usage")
  public void setUsage(long usage) {
    this.usage = usage;
  }
}
