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
public class ExternalIdentifiersPatchImpl implements ExternalIdentifiersPatch {
  @JsonProperty(JsonField.DOIS)
  private ExternalIdentifiersPatch.DoisType dois;

  @JsonProperty(JsonField.HYPERLINKS)
  private ExternalIdentifiersPatch.HyperlinksType hyperlinks;

  @JsonProperty(JsonField.BIOPROJECT_IDS)
  private ExternalIdentifiersPatch.BioprojectIdsType bioprojectIds;

  @JsonProperty(JsonField.DOIS)
  public ExternalIdentifiersPatch.DoisType getDois() {
    return this.dois;
  }

  @JsonProperty(JsonField.DOIS)
  public void setDois(ExternalIdentifiersPatch.DoisType dois) {
    this.dois = dois;
  }

  @JsonProperty(JsonField.HYPERLINKS)
  public ExternalIdentifiersPatch.HyperlinksType getHyperlinks() {
    return this.hyperlinks;
  }

  @JsonProperty(JsonField.HYPERLINKS)
  public void setHyperlinks(ExternalIdentifiersPatch.HyperlinksType hyperlinks) {
    this.hyperlinks = hyperlinks;
  }

  @JsonProperty(JsonField.BIOPROJECT_IDS)
  public ExternalIdentifiersPatch.BioprojectIdsType getBioprojectIds() {
    return this.bioprojectIds;
  }

  @JsonProperty(JsonField.BIOPROJECT_IDS)
  public void setBioprojectIds(ExternalIdentifiersPatch.BioprojectIdsType bioprojectIds) {
    this.bioprojectIds = bioprojectIds;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class BioprojectIdsTypeImpl implements ExternalIdentifiersPatch.BioprojectIdsType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private List<BioprojectIDReference> value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public List<BioprojectIDReference> getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(List<BioprojectIDReference> value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class DoisTypeImpl implements ExternalIdentifiersPatch.DoisType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private List<DOIReference> value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public List<DOIReference> getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(List<DOIReference> value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class HyperlinksTypeImpl implements ExternalIdentifiersPatch.HyperlinksType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private List<DatasetHyperlink> value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public List<DatasetHyperlink> getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(List<DatasetHyperlink> value) {
      this.value = value;
    }
  }
}
