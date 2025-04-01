package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.File;

@JsonDeserialize(
    as = DatasetPostRequestBodyImpl.class
)
public interface DatasetPostRequestBody {
  @JsonProperty("meta")
  DatasetPostMeta getMeta();

  @JsonProperty("meta")
  void setMeta(DatasetPostMeta meta);

  @JsonProperty("file")
  File getFile();

  @JsonProperty("file")
  void setFile(File file);

  @JsonProperty("url")
  String getUrl();

  @JsonProperty("url")
  void setUrl(String url);
}
