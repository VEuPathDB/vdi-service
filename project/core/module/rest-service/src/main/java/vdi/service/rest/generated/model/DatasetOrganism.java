package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetOrganismImpl.class
)
public interface DatasetOrganism {
  @JsonProperty(JsonField.SPECIES)
  String getSpecies();

  @JsonProperty(JsonField.SPECIES)
  void setSpecies(String species);

  @JsonProperty(JsonField.STRAIN)
  String getStrain();

  @JsonProperty(JsonField.STRAIN)
  void setStrain(String strain);
}
