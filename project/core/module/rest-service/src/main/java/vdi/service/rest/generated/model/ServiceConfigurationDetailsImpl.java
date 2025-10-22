package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("daemons")
public class ServiceConfigurationDetailsImpl implements ServiceConfigurationDetails {
  @JsonProperty("daemons")
  private DaemonConfiguration daemons;

  @JsonProperty("daemons")
  public DaemonConfiguration getDaemons() {
    return this.daemons;
  }

  @JsonProperty("daemons")
  public void setDaemons(DaemonConfiguration daemons) {
    this.daemons = daemons;
  }
}
