package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = InstallCleanupRequestImpl.class
)
public interface InstallCleanupRequest {
  @JsonProperty(
      value = "all",
      defaultValue = "false"
  )
  boolean getAll();

  @JsonProperty(
      value = "all",
      defaultValue = "false"
  )
  void setAll(boolean all);

  @JsonProperty("targets")
  List<InstallCleanupTarget> getTargets();

  @JsonProperty("targets")
  void setTargets(List<InstallCleanupTarget> targets);
}
