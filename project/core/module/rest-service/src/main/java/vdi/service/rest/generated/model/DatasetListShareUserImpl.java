package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "userId",
    "firstName",
    "lastName",
    "organization",
    "accepted"
})
public class DatasetListShareUserImpl implements DatasetListShareUser {
  @JsonProperty(JsonField.USER_ID)
  private long userId;

  @JsonProperty(JsonField.FIRST_NAME)
  private String firstName;

  @JsonProperty(JsonField.LAST_NAME)
  private String lastName;

  @JsonProperty(JsonField.ORGANIZATION)
  private String organization;

  @JsonProperty(JsonField.ACCEPTED)
  private boolean accepted;

  @JsonProperty(JsonField.USER_ID)
  public long getUserId() {
    return this.userId;
  }

  @JsonProperty(JsonField.USER_ID)
  public void setUserId(long userId) {
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

  @JsonProperty(JsonField.ORGANIZATION)
  public String getOrganization() {
    return this.organization;
  }

  @JsonProperty(JsonField.ORGANIZATION)
  public void setOrganization(String organization) {
    this.organization = organization;
  }

  @JsonProperty(JsonField.ACCEPTED)
  public boolean getAccepted() {
    return this.accepted;
  }

  @JsonProperty(JsonField.ACCEPTED)
  public void setAccepted(boolean accepted) {
    this.accepted = accepted;
  }
}
