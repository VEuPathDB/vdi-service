package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetContactImpl.class
)
public interface DatasetContact {
  @JsonProperty(JsonField.NAME)
  String getName();

  @JsonProperty(JsonField.NAME)
  void setName(String name);

  @JsonProperty(JsonField.EMAIL)
  String getEmail();

  @JsonProperty(JsonField.EMAIL)
  void setEmail(String email);

  @JsonProperty(JsonField.AFFILIATION)
  String getAffiliation();

  @JsonProperty(JsonField.AFFILIATION)
  void setAffiliation(String affiliation);

  @JsonProperty(JsonField.CITY)
  String getCity();

  @JsonProperty(JsonField.CITY)
  void setCity(String city);

  @JsonProperty(JsonField.STATE)
  String getState();

  @JsonProperty(JsonField.STATE)
  void setState(String state);

  @JsonProperty(JsonField.COUNTRY)
  String getCountry();

  @JsonProperty(JsonField.COUNTRY)
  void setCountry(String country);

  @JsonProperty(JsonField.ADDRESS)
  String getAddress();

  @JsonProperty(JsonField.ADDRESS)
  void setAddress(String address);

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
}
