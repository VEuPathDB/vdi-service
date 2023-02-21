package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetOwnerImpl.class
)
public interface DatasetOwner {
  @JsonProperty("userID")
  Long getUserID();

  @JsonProperty("userID")
  void setUserID(Long userID);

  @JsonProperty("displayName")
  String getDisplayName();

  @JsonProperty("displayName")
  void setDisplayName(String displayName);

  @JsonProperty("organization")
  String getOrganization();

  @JsonProperty("organization")
  void setOrganization(String organization);
}
