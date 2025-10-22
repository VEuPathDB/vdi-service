package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "enabled",
    "fullRunInterval",
    "slimRunInterval",
    "performDeletes"
})
public class ReconcilerConfigImpl implements ReconcilerConfig {
  @JsonProperty(JsonField.ENABLED)
  private Boolean enabled;

  @JsonProperty(JsonField.FULL_RUN_INTERVAL)
  private String fullRunInterval;

  @JsonProperty(JsonField.SLIM_RUN_INTERVAL)
  private String slimRunInterval;

  @JsonProperty(JsonField.PERFORM_DELETES)
  private Boolean performDeletes;

  @JsonProperty(JsonField.ENABLED)
  public Boolean getEnabled() {
    return this.enabled;
  }

  @JsonProperty(JsonField.ENABLED)
  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  @JsonProperty(JsonField.FULL_RUN_INTERVAL)
  public String getFullRunInterval() {
    return this.fullRunInterval;
  }

  @JsonProperty(JsonField.FULL_RUN_INTERVAL)
  public void setFullRunInterval(String fullRunInterval) {
    this.fullRunInterval = fullRunInterval;
  }

  @JsonProperty(JsonField.SLIM_RUN_INTERVAL)
  public String getSlimRunInterval() {
    return this.slimRunInterval;
  }

  @JsonProperty(JsonField.SLIM_RUN_INTERVAL)
  public void setSlimRunInterval(String slimRunInterval) {
    this.slimRunInterval = slimRunInterval;
  }

  @JsonProperty(JsonField.PERFORM_DELETES)
  public Boolean getPerformDeletes() {
    return this.performDeletes;
  }

  @JsonProperty(JsonField.PERFORM_DELETES)
  public void setPerformDeletes(Boolean performDeletes) {
    this.performDeletes = performDeletes;
  }
}
