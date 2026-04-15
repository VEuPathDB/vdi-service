package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "url",
    "version"
})
public class DatasetSourceImpl implements DatasetSource {
  @JsonProperty(JsonField.URL)
  private String url;

  @JsonProperty(JsonField.VERSION)
  private String version;

  @JsonProperty(JsonField.URL)
  public String getUrl() {
    return this.url;
  }

  @JsonProperty(JsonField.URL)
  public void setUrl(String url) {
    this.url = url;
  }

  @JsonProperty(JsonField.VERSION)
  public String getVersion() {
    return this.version;
  }

  @JsonProperty(JsonField.VERSION)
  public void setVersion(String version) {
    this.version = version;
  }
}
