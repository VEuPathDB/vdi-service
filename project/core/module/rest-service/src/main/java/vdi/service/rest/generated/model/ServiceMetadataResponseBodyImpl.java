package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("buildInfo")
public class ServiceMetadataResponseBodyImpl implements ServiceMetadataResponseBody {
  @JsonProperty("buildInfo")
  private ServiceMetadataBuildInfoOutput buildInfo;

  @JsonProperty("buildInfo")
  public ServiceMetadataBuildInfoOutput getBuildInfo() {
    return this.buildInfo;
  }

  @JsonProperty("buildInfo")
  public void setBuildInfo(ServiceMetadataBuildInfoOutput buildInfo) {
    this.buildInfo = buildInfo;
  }
}
