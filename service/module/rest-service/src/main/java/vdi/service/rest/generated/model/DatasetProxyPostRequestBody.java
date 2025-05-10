package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.File;

@JsonDeserialize(
    as = DatasetProxyPostRequestBodyImpl.class
)
public interface DatasetProxyPostRequestBody {
  @JsonProperty(JsonField.META)
  DatasetProxyPostMeta getMeta();

  @JsonProperty(JsonField.META)
  void setMeta(DatasetProxyPostMeta meta);

  @JsonProperty(JsonField.FILE)
  File getFile();

  @JsonProperty(JsonField.FILE)
  void setFile(File file);

  @JsonProperty(JsonField.URL)
  String getUrl();

  @JsonProperty(JsonField.URL)
  void setUrl(String url);
}
