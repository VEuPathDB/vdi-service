package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;

@JsonDeserialize(
    as = HealthResponseImpl.class
)
public interface HealthResponse {
  @JsonProperty(JsonField.STATUS)
  StatusType getStatus();

  @JsonProperty(JsonField.STATUS)
  void setStatus(StatusType status);

  @JsonProperty(JsonField.DEPENDENCIES)
  List<DependencyStatus> getDependencies();

  @JsonProperty(JsonField.DEPENDENCIES)
  void setDependencies(List<DependencyStatus> dependencies);

  @JsonProperty(JsonField.INFO)
  InfoType getInfo();

  @JsonProperty(JsonField.INFO)
  void setInfo(InfoType info);

  enum StatusType {
    @JsonProperty("healthy")
    HEALTHY("healthy"),

    @JsonProperty("unhealthy")
    UNHEALTHY("unhealthy");

    private String name;

    StatusType(String name) {
      this.name = name;
    }
  }

  @JsonDeserialize(
      as = HealthResponseImpl.InfoTypeImpl.class
  )
  interface InfoType {
    @JsonProperty("threads")
    int getThreads();

    @JsonProperty("threads")
    void setThreads(int threads);

    @JsonProperty("uptime")
    String getUptime();

    @JsonProperty("uptime")
    void setUptime(String uptime);

    @JsonProperty("uptimeMillis")
    long getUptimeMillis();

    @JsonProperty("uptimeMillis")
    void setUptimeMillis(long uptimeMillis);

    @JsonAnyGetter
    Map<String, Object> getAdditionalProperties();

    @JsonAnySetter
    void setAdditionalProperties(String key, Object value);
  }
}
