package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "displayName",
    "organization",
    "email"
})
public class ShareOfferRecipientImpl implements ShareOfferRecipient {
  @JsonProperty("displayName")
  private String displayName;

  @JsonProperty("organization")
  private String organization;

  @JsonProperty("email")
  private String email;

  @JsonProperty("displayName")
  public String getDisplayName() {
    return this.displayName;
  }

  @JsonProperty("displayName")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @JsonProperty("organization")
  public String getOrganization() {
    return this.organization;
  }

  @JsonProperty("organization")
  public void setOrganization(String organization) {
    this.organization = organization;
  }

  @JsonProperty("email")
  public String getEmail() {
    return this.email;
  }

  @JsonProperty("email")
  public void setEmail(String email) {
    this.email = email;
  }
}
