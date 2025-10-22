package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("reconciler")
public class DaemonConfigurationImpl implements DaemonConfiguration {
  @JsonProperty("reconciler")
  private ReconcilerConfig reconciler;

  @JsonProperty("reconciler")
  public ReconcilerConfig getReconciler() {
    return this.reconciler;
  }

  @JsonProperty("reconciler")
  public void setReconciler(ReconcilerConfig reconciler) {
    this.reconciler = reconciler;
  }
}
