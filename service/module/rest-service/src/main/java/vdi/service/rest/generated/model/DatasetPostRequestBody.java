package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.File;

@JsonDeserialize(
    as = DatasetPostRequestBodyImpl.class
)
public interface DatasetPostRequestBody {
  @JsonProperty(JsonField.META)
  DatasetPostMeta getMeta();

  @JsonProperty(JsonField.META)
  void setMeta(DatasetPostMeta meta);

  @JsonProperty(JsonField.FILE)
  File getFile();

  @JsonProperty(JsonField.FILE)
  void setFile(File file);

  @JsonProperty(JsonField.URL)
  String getUrl();

  @JsonProperty(JsonField.URL)
  void setUrl(String url);
}
