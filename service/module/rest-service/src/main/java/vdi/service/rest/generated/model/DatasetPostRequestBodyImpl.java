package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.File;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "meta",
    "file",
    "url"
})
public class DatasetPostRequestBodyImpl implements DatasetPostRequestBody {
  @JsonProperty(JsonField.META)
  private DatasetPostMeta meta;

  @JsonProperty(JsonField.FILE)
  private File file;

  @JsonProperty(JsonField.URL)
  private String url;

  @JsonProperty(JsonField.META)
  public DatasetPostMeta getMeta() {
    return this.meta;
  }

  @JsonProperty(JsonField.META)
  public void setMeta(DatasetPostMeta meta) {
    this.meta = meta;
  }

  @JsonProperty(JsonField.FILE)
  public File getFile() {
    return this.file;
  }

  @JsonProperty(JsonField.FILE)
  public void setFile(File file) {
    this.file = file;
  }

  @JsonProperty(JsonField.URL)
  public String getUrl() {
    return this.url;
  }

  @JsonProperty(JsonField.URL)
  public void setUrl(String url) {
    this.url = url;
  }
}
