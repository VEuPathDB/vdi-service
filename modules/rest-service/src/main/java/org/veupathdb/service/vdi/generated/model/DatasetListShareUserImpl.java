package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "userID",
    "firstName",
    "lastName",
    "organization",
    "accepted"
})
public class DatasetListShareUserImpl implements DatasetListShareUser {
  @JsonProperty("userID")
  private Long userID;

  @JsonProperty("firstName")
  private String firstName;

  @JsonProperty("lastName")
  private String lastName;

  @JsonProperty("organization")
  private String organization;

  @JsonProperty("accepted")
  private Boolean accepted;

  @JsonProperty("userID")
  public Long getUserID() {
    return this.userID;
  }

  @JsonProperty("userID")
  public void setUserID(Long userID) {
    this.userID = userID;
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

  @JsonProperty("accepted")
  public Boolean getAccepted() {
    return this.accepted;
  }

  @JsonProperty("accepted")
  public void setAccepted(Boolean accepted) {
    this.accepted = accepted;
  }
}
