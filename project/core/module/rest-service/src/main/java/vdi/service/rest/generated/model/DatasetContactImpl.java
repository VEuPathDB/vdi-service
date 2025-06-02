package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "isPrimary",
    "email",
    "affiliation",
    "city",
    "state",
    "country",
    "address"
})
public class DatasetContactImpl implements DatasetContact {
  @JsonProperty(JsonField.NAME)
  private String name;

  @JsonProperty(
      value = JsonField.IS_PRIMARY,
      defaultValue = "false"
  )
  private Boolean isPrimary;

  @JsonProperty(JsonField.EMAIL)
  private String email;

  @JsonProperty(JsonField.AFFILIATION)
  private String affiliation;

  @JsonProperty(JsonField.CITY)
  private String city;

  @JsonProperty(JsonField.STATE)
  private String state;

  @JsonProperty(JsonField.COUNTRY)
  private String country;

  @JsonProperty(JsonField.ADDRESS)
  private String address;

  @JsonProperty(JsonField.NAME)
  public String getName() {
    return this.name;
  }

  @JsonProperty(JsonField.NAME)
  public void setName(String name) {
    this.name = name;
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

  @JsonProperty(JsonField.CITY)
  public String getCity() {
    return this.city;
  }

  @JsonProperty(JsonField.CITY)
  public void setCity(String city) {
    this.city = city;
  }

  @JsonProperty(JsonField.STATE)
  public String getState() {
    return this.state;
  }

  @JsonProperty(JsonField.STATE)
  public void setState(String state) {
    this.state = state;
  }

  @JsonProperty(JsonField.COUNTRY)
  public String getCountry() {
    return this.country;
  }

  @JsonProperty(JsonField.COUNTRY)
  public void setCountry(String country) {
    this.country = country;
  }

  @JsonProperty(JsonField.ADDRESS)
  public String getAddress() {
    return this.address;
  }

  @JsonProperty(JsonField.ADDRESS)
  public void setAddress(String address) {
    this.address = address;
  }
}
