package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "userId",
    "firstName",
    "lastName",
    "affiliation",
    "email"
})
public class ShareOfferRecipientImpl implements ShareOfferRecipient {
  @JsonProperty(JsonField.USER_ID)
  private Long userId;

  @JsonProperty(JsonField.FIRST_NAME)
  private String firstName;

  @JsonProperty(JsonField.LAST_NAME)
  private String lastName;

  @JsonProperty(JsonField.AFFILIATION)
  private String affiliation;

  @JsonProperty(JsonField.EMAIL)
  private String email;

  @JsonProperty(JsonField.USER_ID)
  public Long getUserId() {
    return this.userId;
  }

  @JsonProperty(JsonField.USER_ID)
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @JsonProperty(JsonField.FIRST_NAME)
  public String getFirstName() {
    return this.firstName;
  }

  @JsonProperty(JsonField.FIRST_NAME)
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @JsonProperty(JsonField.LAST_NAME)
  public String getLastName() {
    return this.lastName;
  }

  @JsonProperty(JsonField.LAST_NAME)
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @JsonProperty(JsonField.AFFILIATION)
  public String getAffiliation() {
    return this.affiliation;
  }

  @JsonProperty(JsonField.AFFILIATION)
  public void setAffiliation(String affiliation) {
    this.affiliation = affiliation;
  }

  @JsonProperty(JsonField.EMAIL)
  public String getEmail() {
    return this.email;
  }

  @JsonProperty(JsonField.EMAIL)
  public void setEmail(String email) {
    this.email = email;
  }
}
