package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "dois",
    "hyperlinks",
    "bioprojectIds"
})
public class ExternalIdentifiersImpl implements ExternalIdentifiers {
  @JsonProperty(JsonField.DOIS)
  private List<DOIReference> dois;

  @JsonProperty(JsonField.HYPERLINKS)
  private List<DatasetHyperlink> hyperlinks;

  @JsonProperty(JsonField.BIOPROJECT_IDS)
  private List<BioprojectIDReference> bioprojectIds;

  @JsonProperty(JsonField.DOIS)
  public List<DOIReference> getDois() {
    return this.dois;
  }

  @JsonProperty(JsonField.DOIS)
  public void setDois(List<DOIReference> dois) {
    this.dois = dois;
  }

  @JsonProperty(JsonField.HYPERLINKS)
  public List<DatasetHyperlink> getHyperlinks() {
    return this.hyperlinks;
  }

  @JsonProperty(JsonField.HYPERLINKS)
  public void setHyperlinks(List<DatasetHyperlink> hyperlinks) {
    this.hyperlinks = hyperlinks;
  }

  @JsonProperty(JsonField.BIOPROJECT_IDS)
  public List<BioprojectIDReference> getBioprojectIds() {
    return this.bioprojectIds;
  }

  @JsonProperty(JsonField.BIOPROJECT_IDS)
  public void setBioprojectIds(List<BioprojectIDReference> bioprojectIds) {
    this.bioprojectIds = bioprojectIds;
  }
}
