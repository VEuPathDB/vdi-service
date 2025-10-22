package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = ServiceConfigurationDetailsImpl.class
)
public interface ServiceConfigurationDetails {
  @JsonProperty("daemons")
  DaemonConfiguration getDaemons();

  @JsonProperty("daemons")
  void setDaemons(DaemonConfiguration daemons);
}
