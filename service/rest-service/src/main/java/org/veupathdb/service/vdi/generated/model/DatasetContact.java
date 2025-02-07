package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetContactImpl.class
)
public interface DatasetContact {
  @JsonProperty("name")
  String getName();

  @JsonProperty("name")
  void setName(String name);

  @JsonProperty("email")
  String getEmail();

  @JsonProperty("email")
  void setEmail(String email);

  @JsonProperty("affiliation")
  String getAffiliation();

  @JsonProperty("affiliation")
  void setAffiliation(String affiliation);

  @JsonProperty("city")
  String getCity();

  @JsonProperty("city")
  void setCity(String city);

  @JsonProperty("state")
  String getState();

  @JsonProperty("state")
  void setState(String state);

  @JsonProperty("country")
  String getCountry();

  @JsonProperty("country")
  void setCountry(String country);

  @JsonProperty("address")
  String getAddress();

  @JsonProperty("address")
  void setAddress(String address);

  @JsonProperty(
      value = "isPrimary",
      defaultValue = "false"
  )
  Boolean getIsPrimary();

  @JsonProperty(
      value = "isPrimary",
      defaultValue = "false"
  )
  void setIsPrimary(Boolean isPrimary);
}
