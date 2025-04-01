package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.File;

@JsonDeserialize(
    as = DatasetPutRequestBodyImpl.class
)
public interface DatasetPutRequestBody {
  @JsonProperty("meta")
  DatasetPutMetadata getMeta();

  @JsonProperty("meta")
  void setMeta(DatasetPutMetadata meta);

  @JsonProperty("file")
  File getFile();

  @JsonProperty("file")
  void setFile(File file);

  @JsonProperty("url")
  String getUrl();

  @JsonProperty("url")
  void setUrl(String url);
}
