package vdi.service.rest.generated.model;

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
  @JsonProperty(JsonField.URL)
  private String url;

  @JsonProperty(JsonField.TEXT)
  private String text;

  @JsonProperty(JsonField.DESCRIPTION)
  private String description;

  @JsonProperty(
      value = JsonField.IS_PUBLICATION,
      defaultValue = "false"
  )
  private Boolean isPublication;

  @JsonProperty(JsonField.URL)
  public String getUrl() {
    return this.url;
  }

  @JsonProperty(JsonField.URL)
  public void setUrl(String url) {
    this.url = url;
  }

  @JsonProperty(JsonField.TEXT)
  public String getText() {
    return this.text;
  }

  @JsonProperty(JsonField.TEXT)
  public void setText(String text) {
    this.text = text;
  }

  @JsonProperty(JsonField.DESCRIPTION)
  public String getDescription() {
    return this.description;
  }

  @JsonProperty(JsonField.DESCRIPTION)
  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty(
      value = JsonField.IS_PUBLICATION,
      defaultValue = "false"
  )
  public Boolean getIsPublication() {
    return this.isPublication;
  }

  @JsonProperty(
      value = JsonField.IS_PUBLICATION,
      defaultValue = "false"
  )
  public void setIsPublication(Boolean isPublication) {
    this.isPublication = isPublication;
  }
}
