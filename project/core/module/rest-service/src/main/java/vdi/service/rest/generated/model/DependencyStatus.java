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
  Boolean getReachable();

  @JsonProperty(JsonField.REACHABLE)
  void setReachable(Boolean reachable);

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

    public final String value;

    public String getValue() {
      return this.value;
    }

    OnlineType(String name) {
      this.value = name;
    }
  }
}
