package org.veupathdb.service.vdi.generated.model;

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
public class DatasetPostRequestImpl implements DatasetPostRequest {
  @JsonProperty("meta")
  private DatasetPostMeta meta;

  @JsonProperty("file")
  private File file;

  @JsonProperty("url")
  private String url;

  @JsonProperty("meta")
  public DatasetPostMeta getMeta() {
    return this.meta;
  }

  @JsonProperty("meta")
  public void setMeta(DatasetPostMeta meta) {
    this.meta = meta;
  }

  @JsonProperty("file")
  public File getFile() {
    return this.file;
  }

  @JsonProperty("file")
  public void setFile(File file) {
    this.file = file;
  }

  @JsonProperty("url")
  public String getUrl() {
    return this.url;
  }

  @JsonProperty("url")
  public void setUrl(String url) {
    this.url = url;
  }
}
