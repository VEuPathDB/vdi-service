package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
    private int threads;

    @JsonProperty("uptime")
    private String uptime;

    @JsonProperty("uptimeMillis")
    private long uptimeMillis;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new ExcludingMap();

    @JsonProperty("threads")
    public int getThreads() {
      return this.threads;
    }

    @JsonProperty("threads")
    public void setThreads(int threads) {
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
    public long getUptimeMillis() {
      return this.uptimeMillis;
    }

    @JsonProperty("uptimeMillis")
    public void setUptimeMillis(long uptimeMillis) {
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
