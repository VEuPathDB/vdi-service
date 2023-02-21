package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = ShareOfferRecipientImpl.class
)
public interface ShareOfferRecipient {
  @JsonProperty("displayName")
  String getDisplayName();

  @JsonProperty("displayName")
  void setDisplayName(String displayName);

  @JsonProperty("organization")
  String getOrganization();

  @JsonProperty("organization")
  void setOrganization(String organization);

  @JsonProperty("email")
  String getEmail();

  @JsonProperty("email")
  void setEmail(String email);
}
