package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = UserQuotaDetailsImpl.class
)
public interface UserQuotaDetails {
  @JsonProperty(JsonField.LIMIT)
  Long getLimit();

  @JsonProperty(JsonField.LIMIT)
  void setLimit(Long limit);

  @JsonProperty(JsonField.USAGE)
  Long getUsage();

  @JsonProperty(JsonField.USAGE)
  void setUsage(Long usage);
}
