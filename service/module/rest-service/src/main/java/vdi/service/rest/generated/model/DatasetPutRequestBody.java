package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.File;

@JsonDeserialize(
    as = DatasetPutRequestBodyImpl.class
)
public interface DatasetPutRequestBody {
  @JsonProperty(JsonField.META)
  DatasetPutMetadata getMeta();

  @JsonProperty(JsonField.META)
  void setMeta(DatasetPutMetadata meta);

  @JsonProperty(JsonField.FILE)
  File getFile();

  @JsonProperty(JsonField.FILE)
  void setFile(File file);

  @JsonProperty(JsonField.URL)
  String getUrl();

  @JsonProperty(JsonField.URL)
  void setUrl(String url);
}
