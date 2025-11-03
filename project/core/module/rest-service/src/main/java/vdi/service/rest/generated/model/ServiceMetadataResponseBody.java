package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = ServiceMetadataResponseBodyImpl.class
)
public interface ServiceMetadataResponseBody {
  @JsonProperty("buildInfo")
  ServiceMetadataBuildInfoOutput getBuildInfo();

  @JsonProperty("buildInfo")
  void setBuildInfo(ServiceMetadataBuildInfoOutput buildInfo);
}
