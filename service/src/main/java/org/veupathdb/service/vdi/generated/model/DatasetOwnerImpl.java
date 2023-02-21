package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "userID",
    "displayName",
    "organization"
})
public class DatasetOwnerImpl implements DatasetOwner {
  @JsonProperty("userID")
  private Long userID;

  @JsonProperty("displayName")
  private String displayName;

  @JsonProperty("organization")
  private String organization;

  @JsonProperty("userID")
  public Long getUserID() {
    return this.userID;
  }

  @JsonProperty("userID")
  public void setUserID(Long userID) {
    this.userID = userID;
  }

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
}
