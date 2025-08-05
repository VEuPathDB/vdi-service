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
  private long limit;

  @JsonProperty(JsonField.USAGE)
  private long usage;

  @JsonProperty(JsonField.LIMIT)
  public long getLimit() {
    return this.limit;
  }

  @JsonProperty(JsonField.LIMIT)
  public void setLimit(long limit) {
    this.limit = limit;
  }

  @JsonProperty(JsonField.USAGE)
  public long getUsage() {
    return this.usage;
  }

  @JsonProperty(JsonField.USAGE)
  public void setUsage(long usage) {
    this.usage = usage;
  }
}
