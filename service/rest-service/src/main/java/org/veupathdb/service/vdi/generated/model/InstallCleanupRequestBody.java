package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = InstallCleanupRequestBodyImpl.class
)
public interface InstallCleanupRequestBody {
  @JsonProperty(
      value = "all",
      defaultValue = "false"
  )
  Boolean getAll();

  @JsonProperty(
      value = "all",
      defaultValue = "false"
  )
  void setAll(Boolean all);

  @JsonProperty("targets")
  List<InstallCleanupTarget> getTargets();

  @JsonProperty("targets")
  void setTargets(List<InstallCleanupTarget> targets);
}
