package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("quota")
public class UserMetadataImpl implements UserMetadata {
  @JsonProperty("quota")
  private UserQuotaDetails quota;

  @JsonProperty("quota")
  public UserQuotaDetails getQuota() {
    return this.quota;
  }

  @JsonProperty("quota")
  public void setQuota(UserQuotaDetails quota) {
    this.quota = quota;
  }
}
