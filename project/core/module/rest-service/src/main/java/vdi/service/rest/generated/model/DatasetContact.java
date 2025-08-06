package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetContactImpl.class
)
public interface DatasetContact {
  @JsonProperty(JsonField.FIRST_NAME)
  String getFirstName();

  @JsonProperty(JsonField.FIRST_NAME)
  void setFirstName(String firstName);

  @JsonProperty(JsonField.MIDDLE_NAME)
  String getMiddleName();

  @JsonProperty(JsonField.MIDDLE_NAME)
  void setMiddleName(String middleName);

  @JsonProperty(JsonField.LAST_NAME)
  String getLastName();

  @JsonProperty(JsonField.LAST_NAME)
  void setLastName(String lastName);

  @JsonProperty(
      value = JsonField.IS_PRIMARY,
      defaultValue = "false"
  )
  Boolean getIsPrimary();

  @JsonProperty(
      value = JsonField.IS_PRIMARY,
      defaultValue = "false"
  )
  void setIsPrimary(Boolean isPrimary);

  @JsonProperty(JsonField.EMAIL)
  String getEmail();

  @JsonProperty(JsonField.EMAIL)
  void setEmail(String email);

  @JsonProperty(JsonField.AFFILIATION)
  String getAffiliation();

  @JsonProperty(JsonField.AFFILIATION)
  void setAffiliation(String affiliation);

  @JsonProperty(JsonField.COUNTRY)
  String getCountry();

  @JsonProperty(JsonField.COUNTRY)
  void setCountry(String country);

  @JsonProperty(JsonField.CITY)
  String getCity();

  @JsonProperty(JsonField.CITY)
  void setCity(String city);

  @JsonProperty(JsonField.STATE)
  String getState();

  @JsonProperty(JsonField.STATE)
  void setState(String state);

  @JsonProperty(JsonField.ADDRESS)
  String getAddress();

  @JsonProperty(JsonField.ADDRESS)
  void setAddress(String address);
}
