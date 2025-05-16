package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = InstallCleanupRequestBodyImpl.class
)
public interface InstallCleanupRequestBody {
  @JsonProperty(
      value = JsonField.ALL,
      defaultValue = "false"
  )
  Boolean getAll();

  @JsonProperty(
      value = JsonField.ALL,
      defaultValue = "false"
  )
  void setAll(Boolean all);

  @JsonProperty(JsonField.TARGETS)
  List<InstallCleanupTarget> getTargets();

  @JsonProperty(JsonField.TARGETS)
  void setTargets(List<InstallCleanupTarget> targets);
}
