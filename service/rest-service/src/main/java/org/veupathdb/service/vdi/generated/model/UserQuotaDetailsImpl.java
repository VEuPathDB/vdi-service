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
  private Long limit;

  @JsonProperty("usage")
  private Long usage;

  @JsonProperty("limit")
  public Long getLimit() {
    return this.limit;
  }

  @JsonProperty("limit")
  public void setLimit(Long limit) {
    this.limit = limit;
  }

  @JsonProperty("usage")
  public Long getUsage() {
    return this.usage;
  }

  @JsonProperty("usage")
  public void setUsage(Long usage) {
    this.usage = usage;
  }
}
