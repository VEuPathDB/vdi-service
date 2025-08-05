package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetOwnerImpl.class
)
public interface DatasetOwner {
  @JsonProperty(JsonField.USER_ID)
  long getUserId();

  @JsonProperty(JsonField.USER_ID)
  void setUserId(long userId);

  @JsonProperty(JsonField.FIRST_NAME)
  String getFirstName();

  @JsonProperty(JsonField.FIRST_NAME)
  void setFirstName(String firstName);

  @JsonProperty(JsonField.LAST_NAME)
  String getLastName();

  @JsonProperty(JsonField.LAST_NAME)
  void setLastName(String lastName);

  @JsonProperty(JsonField.EMAIL)
  String getEmail();

  @JsonProperty(JsonField.EMAIL)
  void setEmail(String email);

  @JsonProperty(JsonField.ORGANIZATION)
  String getOrganization();

  @JsonProperty(JsonField.ORGANIZATION)
  void setOrganization(String organization);
}
