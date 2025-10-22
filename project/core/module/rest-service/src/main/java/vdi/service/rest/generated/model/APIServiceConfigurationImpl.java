package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "maxUploadSize",
    "userMaxStorageSize",
    "enableJerseyTrace"
})
public class APIServiceConfigurationImpl implements APIServiceConfiguration {
  @JsonProperty(JsonField.MAX_UPLOAD_SIZE)
  private Long maxUploadSize;

  @JsonProperty(JsonField.USER_MAX_STORAGE_SIZE)
  private Long userMaxStorageSize;

  @JsonProperty(JsonField.ENABLE_JERSEY_TRACE)
  private Boolean enableJerseyTrace;

  @JsonProperty(JsonField.MAX_UPLOAD_SIZE)
  public Long getMaxUploadSize() {
    return this.maxUploadSize;
  }

  @JsonProperty(JsonField.MAX_UPLOAD_SIZE)
  public void setMaxUploadSize(Long maxUploadSize) {
    this.maxUploadSize = maxUploadSize;
  }

  @JsonProperty(JsonField.USER_MAX_STORAGE_SIZE)
  public Long getUserMaxStorageSize() {
    return this.userMaxStorageSize;
  }

  @JsonProperty(JsonField.USER_MAX_STORAGE_SIZE)
  public void setUserMaxStorageSize(Long userMaxStorageSize) {
    this.userMaxStorageSize = userMaxStorageSize;
  }

  @JsonProperty(JsonField.ENABLE_JERSEY_TRACE)
  public Boolean getEnableJerseyTrace() {
    return this.enableJerseyTrace;
  }

  @JsonProperty(JsonField.ENABLE_JERSEY_TRACE)
  public void setEnableJerseyTrace(Boolean enableJerseyTrace) {
    this.enableJerseyTrace = enableJerseyTrace;
  }
}
