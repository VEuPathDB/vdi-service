package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = ServiceMetadataResponseBodyImpl.class
)
public interface ServiceMetadataResponseBody {
  @JsonProperty(JsonField.BUILD_INFO)
  ServiceMetadataBuildInfoOutput getBuildInfo();

  @JsonProperty(JsonField.BUILD_INFO)
  void setBuildInfo(ServiceMetadataBuildInfoOutput buildInfo);

  @JsonProperty(JsonField.CONFIGURATION)
  ServiceConfigurationDetails getConfiguration();

  @JsonProperty(JsonField.CONFIGURATION)
  void setConfiguration(ServiceConfigurationDetails configuration);
}
