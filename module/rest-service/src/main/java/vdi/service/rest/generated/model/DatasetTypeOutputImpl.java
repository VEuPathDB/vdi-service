package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "version",
    "displayName"
})
public class DatasetTypeOutputImpl implements DatasetTypeOutput {
  @JsonProperty(JsonField.NAME)
  private String name;

  @JsonProperty(JsonField.VERSION)
  private String version;

  @JsonProperty(JsonField.DISPLAY_NAME)
  private String displayName;

  @JsonProperty(JsonField.NAME)
  public String getName() {
    return this.name;
  }

  @JsonProperty(JsonField.NAME)
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty(JsonField.VERSION)
  public String getVersion() {
    return this.version;
  }

  @JsonProperty(JsonField.VERSION)
  public void setVersion(String version) {
    this.version = version;
  }

  @JsonProperty(JsonField.DISPLAY_NAME)
  public String getDisplayName() {
    return this.displayName;
  }

  @JsonProperty(JsonField.DISPLAY_NAME)
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
}
