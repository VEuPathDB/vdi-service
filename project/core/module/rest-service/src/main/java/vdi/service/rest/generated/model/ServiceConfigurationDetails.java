package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = ServiceConfigurationDetailsImpl.class
)
public interface ServiceConfigurationDetails {
  @JsonProperty(JsonField.API)
  APIServiceConfiguration getApi();

  @JsonProperty(JsonField.API)
  void setApi(APIServiceConfiguration api);

  @JsonProperty(JsonField.DAEMONS)
  DaemonConfiguration getDaemons();

  @JsonProperty(JsonField.DAEMONS)
  void setDaemons(DaemonConfiguration daemons);

  @JsonProperty(JsonField.INSTALL_TARGETS)
  List<InstallTarget> getInstallTargets();

  @JsonProperty(JsonField.INSTALL_TARGETS)
  void setInstallTargets(List<InstallTarget> installTargets);
}
