package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "api",
    "daemons",
    "installTargets"
})
public class ServiceConfigurationDetailsImpl implements ServiceConfigurationDetails {
  @JsonProperty(JsonField.API)
  private APIServiceConfiguration api;

  @JsonProperty(JsonField.DAEMONS)
  private DaemonConfiguration daemons;

  @JsonProperty(JsonField.INSTALL_TARGETS)
  private List<InstallTarget> installTargets;

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

  @JsonProperty(JsonField.INSTALL_TARGETS)
  public List<InstallTarget> getInstallTargets() {
    return this.installTargets;
  }

  @JsonProperty(JsonField.INSTALL_TARGETS)
  public void setInstallTargets(List<InstallTarget> installTargets) {
    this.installTargets = installTargets;
  }
}
