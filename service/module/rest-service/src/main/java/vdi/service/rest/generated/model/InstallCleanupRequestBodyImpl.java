package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "all",
    "targets"
})
public class InstallCleanupRequestBodyImpl implements InstallCleanupRequestBody {
  @JsonProperty(
      value = JsonField.ALL,
      defaultValue = "false"
  )
  private Boolean all;

  @JsonProperty(JsonField.TARGETS)
  private List<InstallCleanupTarget> targets;

  @JsonProperty(
      value = JsonField.ALL,
      defaultValue = "false"
  )
  public Boolean getAll() {
    return this.all;
  }

  @JsonProperty(
      value = JsonField.ALL,
      defaultValue = "false"
  )
  public void setAll(Boolean all) {
    this.all = all;
  }

  @JsonProperty(JsonField.TARGETS)
  public List<InstallCleanupTarget> getTargets() {
    return this.targets;
  }

  @JsonProperty(JsonField.TARGETS)
  public void setTargets(List<InstallCleanupTarget> targets) {
    this.targets = targets;
  }
}
