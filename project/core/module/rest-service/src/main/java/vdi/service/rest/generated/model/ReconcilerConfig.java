package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = ReconcilerConfigImpl.class
)
public interface ReconcilerConfig {
  @JsonProperty(JsonField.ENABLED)
  Boolean getEnabled();

  @JsonProperty(JsonField.ENABLED)
  void setEnabled(Boolean enabled);

  @JsonProperty(JsonField.FULL_RUN_INTERVAL)
  String getFullRunInterval();

  @JsonProperty(JsonField.FULL_RUN_INTERVAL)
  void setFullRunInterval(String fullRunInterval);

  @JsonProperty(JsonField.SLIM_RUN_INTERVAL)
  String getSlimRunInterval();

  @JsonProperty(JsonField.SLIM_RUN_INTERVAL)
  void setSlimRunInterval(String slimRunInterval);

  @JsonProperty(JsonField.PERFORM_DELETES)
  Boolean getPerformDeletes();

  @JsonProperty(JsonField.PERFORM_DELETES)
  void setPerformDeletes(Boolean performDeletes);
}
