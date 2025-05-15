package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.*;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "status",
    "dependencies",
    "info"
})
public class HealthResponseImpl implements HealthResponse {
  @JsonProperty(JsonField.STATUS)
  private HealthResponse.StatusType status;

  @JsonProperty(JsonField.DEPENDENCIES)
  private List<DependencyStatus> dependencies;

  @JsonProperty(JsonField.INFO)
  private HealthResponse.InfoType info;

  @JsonProperty(JsonField.STATUS)
  public HealthResponse.StatusType getStatus() {
    return this.status;
  }

  @JsonProperty(JsonField.STATUS)
  public void setStatus(HealthResponse.StatusType status) {
    this.status = status;
  }

  @JsonProperty(JsonField.DEPENDENCIES)
  public List<DependencyStatus> getDependencies() {
    return this.dependencies;
  }

  @JsonProperty(JsonField.DEPENDENCIES)
  public void setDependencies(List<DependencyStatus> dependencies) {
    this.dependencies = dependencies;
  }

  @JsonProperty(JsonField.INFO)
  public HealthResponse.InfoType getInfo() {
    return this.info;
  }

  @JsonProperty(JsonField.INFO)
  public void setInfo(HealthResponse.InfoType info) {
    this.info = info;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "threads",
      "uptime",
      "uptimeMillis"
  })
  public static class InfoTypeImpl implements HealthResponse.InfoType {
    @JsonProperty("threads")
    private Integer threads;

    @JsonProperty("uptime")
    private String uptime;

    @JsonProperty("uptimeMillis")
    private Long uptimeMillis;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new ExcludingMap();

    @JsonProperty("threads")
    public Integer getThreads() {
      return this.threads;
    }

    @JsonProperty("threads")
    public void setThreads(Integer threads) {
      this.threads = threads;
    }

    @JsonProperty("uptime")
    public String getUptime() {
      return this.uptime;
    }

    @JsonProperty("uptime")
    public void setUptime(String uptime) {
      this.uptime = uptime;
    }

    @JsonProperty("uptimeMillis")
    public Long getUptimeMillis() {
      return this.uptimeMillis;
    }

    @JsonProperty("uptimeMillis")
    public void setUptimeMillis(Long uptimeMillis) {
      this.uptimeMillis = uptimeMillis;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
      return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String key, Object value) {
      this.additionalProperties.put(key, value);
    }
  }
}
