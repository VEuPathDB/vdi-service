package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DependencyStatusImpl.class
)
public interface DependencyStatus {
  @JsonProperty(JsonField.NAME)
  String getName();

  @JsonProperty(JsonField.NAME)
  void setName(String name);

  @JsonProperty(JsonField.REACHABLE)
  boolean getReachable();

  @JsonProperty(JsonField.REACHABLE)
  void setReachable(boolean reachable);

  @JsonProperty(JsonField.ONLINE)
  OnlineType getOnline();

  @JsonProperty(JsonField.ONLINE)
  void setOnline(OnlineType online);

  enum OnlineType {
    @JsonProperty("yes")
    YES("yes"),

    @JsonProperty("unknown")
    UNKNOWN("unknown"),

    @JsonProperty("no")
    NO("no");

    private String name;

    OnlineType(String name) {
      this.name = name;
    }
  }
}
