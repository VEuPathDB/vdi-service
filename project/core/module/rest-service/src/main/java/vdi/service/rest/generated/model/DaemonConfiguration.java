package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DaemonConfigurationImpl.class
)
public interface DaemonConfiguration {
  @JsonProperty("reconciler")
  ReconcilerConfig getReconciler();

  @JsonProperty("reconciler")
  void setReconciler(ReconcilerConfig reconciler);
}
