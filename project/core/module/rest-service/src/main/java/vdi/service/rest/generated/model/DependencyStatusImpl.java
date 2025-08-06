package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "reachable",
    "online"
})
public class DependencyStatusImpl implements DependencyStatus {
  @JsonProperty(JsonField.NAME)
  private String name;

  @JsonProperty(JsonField.REACHABLE)
  private Boolean reachable;

  @JsonProperty(JsonField.ONLINE)
  private DependencyStatus.OnlineType online;

  @JsonProperty(JsonField.NAME)
  public String getName() {
    return this.name;
  }

  @JsonProperty(JsonField.NAME)
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty(JsonField.REACHABLE)
  public Boolean getReachable() {
    return this.reachable;
  }

  @JsonProperty(JsonField.REACHABLE)
  public void setReachable(Boolean reachable) {
    this.reachable = reachable;
  }

  @JsonProperty(JsonField.ONLINE)
  public DependencyStatus.OnlineType getOnline() {
    return this.online;
  }

  @JsonProperty(JsonField.ONLINE)
  public void setOnline(DependencyStatus.OnlineType online) {
    this.online = online;
  }
}
