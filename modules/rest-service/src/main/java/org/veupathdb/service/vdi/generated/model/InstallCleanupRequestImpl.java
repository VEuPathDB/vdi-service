package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "all",
    "targets"
})
public class InstallCleanupRequestImpl implements InstallCleanupRequest {
  @JsonProperty(
      value = "all",
      defaultValue = "false"
  )
  private boolean all;

  @JsonProperty("targets")
  private List<InstallCleanupTarget> targets;

  @JsonProperty(
      value = "all",
      defaultValue = "false"
  )
  public boolean getAll() {
    return this.all;
  }

  @JsonProperty(
      value = "all",
      defaultValue = "false"
  )
  public void setAll(boolean all) {
    this.all = all;
  }

  @JsonProperty("targets")
  public List<InstallCleanupTarget> getTargets() {
    return this.targets;
  }

  @JsonProperty("targets")
  public void setTargets(List<InstallCleanupTarget> targets) {
    this.targets = targets;
  }
}
