package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetHyperlinkImpl.class
)
public interface DatasetHyperlink {
  @JsonProperty(JsonField.URL)
  String getUrl();

  @JsonProperty(JsonField.URL)
  void setUrl(String url);

  @JsonProperty(JsonField.TEXT)
  String getText();

  @JsonProperty(JsonField.TEXT)
  void setText(String text);

  @JsonProperty(JsonField.DESCRIPTION)
  String getDescription();

  @JsonProperty(JsonField.DESCRIPTION)
  void setDescription(String description);

  @JsonProperty(
      value = JsonField.IS_PUBLICATION,
      defaultValue = "false"
  )
  Boolean getIsPublication();

  @JsonProperty(
      value = JsonField.IS_PUBLICATION,
      defaultValue = "false"
  )
  void setIsPublication(Boolean isPublication);
}
