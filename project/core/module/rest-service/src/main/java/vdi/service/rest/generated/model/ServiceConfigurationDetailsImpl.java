package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "api",
    "daemons"
})
public class ServiceConfigurationDetailsImpl implements ServiceConfigurationDetails {
  @JsonProperty(JsonField.API)
  private APIServiceConfiguration api;

  @JsonProperty(JsonField.DAEMONS)
  private DaemonConfiguration daemons;

  @JsonProperty(JsonField.API)
  public APIServiceConfiguration getApi() {
    return this.api;
  }

  @JsonProperty(JsonField.API)
  public void setApi(APIServiceConfiguration api) {
    this.api = api;
  }

  @JsonProperty(JsonField.DAEMONS)
  public DaemonConfiguration getDaemons() {
    return this.daemons;
  }

  @JsonProperty(JsonField.DAEMONS)
  public void setDaemons(DaemonConfiguration daemons) {
    this.daemons = daemons;
  }
}
