package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = ShareOfferRecipientImpl.class
)
public interface ShareOfferRecipient {
  @JsonProperty("userId")
  Long getUserId();

  @JsonProperty("userId")
  void setUserId(Long userId);

  @JsonProperty("firstName")
  String getFirstName();

  @JsonProperty("firstName")
  void setFirstName(String firstName);

  @JsonProperty("lastName")
  String getLastName();

  @JsonProperty("lastName")
  void setLastName(String lastName);

  @JsonProperty("organization")
  String getOrganization();

  @JsonProperty("organization")
  void setOrganization(String organization);

  @JsonProperty("email")
  String getEmail();

  @JsonProperty("email")
  void setEmail(String email);
}
