package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "userId",
    "firstName",
    "lastName",
    "organization",
    "email"
})
public class ShareOfferRecipientImpl implements ShareOfferRecipient {
  @JsonProperty("userId")
  private Long userId;

  @JsonProperty("firstName")
  private String firstName;

  @JsonProperty("lastName")
  private String lastName;

  @JsonProperty("organization")
  private String organization;

  @JsonProperty("email")
  private String email;

  @JsonProperty("userId")
  public Long getUserId() {
    return this.userId;
  }

  @JsonProperty("userId")
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @JsonProperty("firstName")
  public String getFirstName() {
    return this.firstName;
  }

  @JsonProperty("firstName")
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @JsonProperty("lastName")
  public String getLastName() {
    return this.lastName;
  }

  @JsonProperty("lastName")
  public void setLastName(String lastName) {
    this.lastName = lastName;
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
