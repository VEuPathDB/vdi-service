package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = UserMetadataImpl.class
)
public interface UserMetadata {
  @JsonProperty("quota")
  UserQuotaDetails getQuota();

  @JsonProperty("quota")
  void setQuota(UserQuotaDetails quota);
}
