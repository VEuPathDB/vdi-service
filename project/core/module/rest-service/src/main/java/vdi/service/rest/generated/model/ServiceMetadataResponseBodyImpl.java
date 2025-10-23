package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "buildInfo",
    "configuration",
    "features"
})
public class ServiceMetadataResponseBodyImpl implements ServiceMetadataResponseBody {
  @JsonProperty(JsonField.BUILD_INFO)
  private ServiceMetadataBuildInfoOutput buildInfo;

  @JsonProperty(JsonField.CONFIGURATION)
  private ServiceConfigurationDetails configuration;

  @JsonProperty(JsonField.FEATURES)
  private ServiceFeatures features;

  @JsonProperty(JsonField.BUILD_INFO)
  public ServiceMetadataBuildInfoOutput getBuildInfo() {
    return this.buildInfo;
  }

  @JsonProperty(JsonField.BUILD_INFO)
  public void setBuildInfo(ServiceMetadataBuildInfoOutput buildInfo) {
    this.buildInfo = buildInfo;
  }

  @JsonProperty(JsonField.CONFIGURATION)
  public ServiceConfigurationDetails getConfiguration() {
    return this.configuration;
  }

  @JsonProperty(JsonField.CONFIGURATION)
  public void setConfiguration(ServiceConfigurationDetails configuration) {
    this.configuration = configuration;
  }

  @JsonProperty(JsonField.FEATURES)
  public ServiceFeatures getFeatures() {
    return this.features;
  }

  @JsonProperty(JsonField.FEATURES)
  public void setFeatures(ServiceFeatures features) {
    this.features = features;
  }
}
