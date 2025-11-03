package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = ExternalIdentifiersImpl.class
)
public interface ExternalIdentifiers {
  @JsonProperty(JsonField.DOIS)
  List<DOIReference> getDois();

  @JsonProperty(JsonField.DOIS)
  void setDois(List<DOIReference> dois);

  @JsonProperty(JsonField.HYPERLINKS)
  List<DatasetHyperlink> getHyperlinks();

  @JsonProperty(JsonField.HYPERLINKS)
  void setHyperlinks(List<DatasetHyperlink> hyperlinks);

  @JsonProperty(JsonField.BIOPROJECT_IDS)
  List<BioprojectIDReference> getBioprojectIds();

  @JsonProperty(JsonField.BIOPROJECT_IDS)
  void setBioprojectIds(List<BioprojectIDReference> bioprojectIds);
}
