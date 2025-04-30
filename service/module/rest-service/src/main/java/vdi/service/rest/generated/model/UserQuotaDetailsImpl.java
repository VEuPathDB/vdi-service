package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "limit",
    "usage"
})
public class UserQuotaDetailsImpl implements UserQuotaDetails {
  @JsonProperty(JsonField.LIMIT)
  private Long limit;

  @JsonProperty(JsonField.USAGE)
  private Long usage;

  @JsonProperty(JsonField.LIMIT)
  public Long getLimit() {
    return this.limit;
  }

  @JsonProperty(JsonField.LIMIT)
  public void setLimit(Long limit) {
    this.limit = limit;
  }

  @JsonProperty(JsonField.USAGE)
  public Long getUsage() {
    return this.usage;
  }

  @JsonProperty(JsonField.USAGE)
  public void setUsage(Long usage) {
    this.usage = usage;
  }
}
