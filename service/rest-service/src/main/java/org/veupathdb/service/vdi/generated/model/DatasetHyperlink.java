package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetHyperlinkImpl.class
)
public interface DatasetHyperlink {
  @JsonProperty("url")
  String getUrl();

  @JsonProperty("url")
  void setUrl(String url);

  @JsonProperty("text")
  String getText();

  @JsonProperty("text")
  void setText(String text);

  @JsonProperty("description")
  String getDescription();

  @JsonProperty("description")
  void setDescription(String description);

  @JsonProperty(
      value = "isPublication",
      defaultValue = "false"
  )
  Boolean getIsPublication();

  @JsonProperty(
      value = "isPublication",
      defaultValue = "false"
  )
  void setIsPublication(Boolean isPublication);
}
