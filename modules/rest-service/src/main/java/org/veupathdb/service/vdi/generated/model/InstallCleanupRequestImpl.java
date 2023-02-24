package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "authorization",
    "all",
    "targets"
})
public class InstallCleanupRequestImpl implements InstallCleanupRequest {
  @JsonProperty("authorization")
  private String authorization;

  @JsonProperty(
      value = "all",
      defaultValue = "false"
  )
  private Boolean all;

  @JsonProperty("targets")
  private List<InstallCleanupTarget> targets;

  @JsonProperty("authorization")
  public String getAuthorization() {
    return this.authorization;
  }

  @JsonProperty("authorization")
  public void setAuthorization(String authorization) {
    this.authorization = authorization;
  }

  @JsonProperty(
      value = "all",
      defaultValue = "false"
  )
  public Boolean getAll() {
    return this.all;
  }

  @JsonProperty(
      value = "all",
      defaultValue = "false"
  )
  public void setAll(Boolean all) {
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
