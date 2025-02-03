package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "url",
    "text",
    "description",
    "isPublication"
})
public class DatasetHyperlinkImpl implements DatasetHyperlink {
  @JsonProperty("url")
  private String url;

  @JsonProperty("text")
  private String text;

  @JsonProperty("description")
  private String description;

  @JsonProperty(
      value = "isPublication",
      defaultValue = "false"
  )
  private Boolean isPublication;

  @JsonProperty("url")
  public String getUrl() {
    return this.url;
  }

  @JsonProperty("url")
  public void setUrl(String url) {
    this.url = url;
  }

  @JsonProperty("text")
  public String getText() {
    return this.text;
  }

  @JsonProperty("text")
  public void setText(String text) {
    this.text = text;
  }

  @JsonProperty("description")
  public String getDescription() {
    return this.description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty(
      value = "isPublication",
      defaultValue = "false"
  )
  public Boolean getIsPublication() {
    return this.isPublication;
  }

  @JsonProperty(
      value = "isPublication",
      defaultValue = "false"
  )
  public void setIsPublication(Boolean isPublication) {
    this.isPublication = isPublication;
  }
}
