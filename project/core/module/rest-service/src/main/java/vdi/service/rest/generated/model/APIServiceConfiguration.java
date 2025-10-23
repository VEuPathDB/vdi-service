package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = APIServiceConfigurationImpl.class
)
public interface APIServiceConfiguration {
  @JsonProperty(JsonField.MAX_UPLOAD_SIZE)
  Long getMaxUploadSize();

  @JsonProperty(JsonField.MAX_UPLOAD_SIZE)
  void setMaxUploadSize(Long maxUploadSize);

  @JsonProperty(JsonField.USER_MAX_STORAGE_SIZE)
  Long getUserMaxStorageSize();

  @JsonProperty(JsonField.USER_MAX_STORAGE_SIZE)
  void setUserMaxStorageSize(Long userMaxStorageSize);
}
