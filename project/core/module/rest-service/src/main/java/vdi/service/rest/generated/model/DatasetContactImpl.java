package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "firstName",
    "middleName",
    "lastName",
    "isPrimary",
    "email",
    "affiliation",
    "country"
})
public class DatasetContactImpl implements DatasetContact {
  @JsonProperty(JsonField.FIRST_NAME)
  private String firstName;

  @JsonProperty(JsonField.MIDDLE_NAME)
  private String middleName;

  @JsonProperty(JsonField.LAST_NAME)
  private String lastName;

  @JsonProperty(
      value = JsonField.IS_PRIMARY,
      defaultValue = "false"
  )
  private Boolean isPrimary;

  @JsonProperty(JsonField.EMAIL)
  private String email;

  @JsonProperty(JsonField.AFFILIATION)
  private String affiliation;

  @JsonProperty(JsonField.COUNTRY)
  private String country;

  @JsonProperty(JsonField.FIRST_NAME)
  public String getFirstName() {
    return this.firstName;
  }

  @JsonProperty(JsonField.FIRST_NAME)
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @JsonProperty(JsonField.MIDDLE_NAME)
  public String getMiddleName() {
    return this.middleName;
  }

  @JsonProperty(JsonField.MIDDLE_NAME)
  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  @JsonProperty(JsonField.LAST_NAME)
  public String getLastName() {
    return this.lastName;
  }

  @JsonProperty(JsonField.LAST_NAME)
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @JsonProperty(
      value = JsonField.IS_PRIMARY,
      defaultValue = "false"
  )
  public Boolean getIsPrimary() {
    return this.isPrimary;
  }

  @JsonProperty(
      value = JsonField.IS_PRIMARY,
      defaultValue = "false"
  )
  public void setIsPrimary(Boolean isPrimary) {
    this.isPrimary = isPrimary;
  }

  @JsonProperty(JsonField.EMAIL)
  public String getEmail() {
    return this.email;
  }

  @JsonProperty(JsonField.EMAIL)
  public void setEmail(String email) {
    this.email = email;
  }

  @JsonProperty(JsonField.AFFILIATION)
  public String getAffiliation() {
    return this.affiliation;
  }

  @JsonProperty(JsonField.AFFILIATION)
  public void setAffiliation(String affiliation) {
    this.affiliation = affiliation;
  }

  @JsonProperty(JsonField.COUNTRY)
  public String getCountry() {
    return this.country;
  }

  @JsonProperty(JsonField.COUNTRY)
  public void setCountry(String country) {
    this.country = country;
  }
}
