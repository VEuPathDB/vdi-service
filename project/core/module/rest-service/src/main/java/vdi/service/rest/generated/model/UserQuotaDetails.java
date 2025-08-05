package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = UserQuotaDetailsImpl.class
)
public interface UserQuotaDetails {
  @JsonProperty(JsonField.LIMIT)
  long getLimit();

  @JsonProperty(JsonField.LIMIT)
  void setLimit(long limit);

  @JsonProperty(JsonField.USAGE)
  long getUsage();

  @JsonProperty(JsonField.USAGE)
  void setUsage(long usage);
}
