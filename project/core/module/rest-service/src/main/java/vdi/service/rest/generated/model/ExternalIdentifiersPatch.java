package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = ExternalIdentifiersPatchImpl.class
)
public interface ExternalIdentifiersPatch {
  @JsonProperty(JsonField.DOIS)
  DoisType getDois();

  @JsonProperty(JsonField.DOIS)
  void setDois(DoisType dois);

  @JsonProperty(JsonField.HYPERLINKS)
  HyperlinksType getHyperlinks();

  @JsonProperty(JsonField.HYPERLINKS)
  void setHyperlinks(HyperlinksType hyperlinks);

  @JsonProperty(JsonField.BIOPROJECT_IDS)
  BioprojectIdsType getBioprojectIds();

  @JsonProperty(JsonField.BIOPROJECT_IDS)
  void setBioprojectIds(BioprojectIdsType bioprojectIds);

  @JsonDeserialize(
      as = ExternalIdentifiersPatchImpl.BioprojectIdsTypeImpl.class
  )
  interface BioprojectIdsType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    List<BioprojectIDReference> getValue();

    @JsonProperty("value")
    void setValue(List<BioprojectIDReference> value);
  }

  @JsonDeserialize(
      as = ExternalIdentifiersPatchImpl.DoisTypeImpl.class
  )
  interface DoisType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    List<DOIReference> getValue();

    @JsonProperty("value")
    void setValue(List<DOIReference> value);
  }

  @JsonDeserialize(
      as = ExternalIdentifiersPatchImpl.HyperlinksTypeImpl.class
  )
  interface HyperlinksType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    List<DatasetHyperlink> getValue();

    @JsonProperty("value")
    void setValue(List<DatasetHyperlink> value);
  }
}
