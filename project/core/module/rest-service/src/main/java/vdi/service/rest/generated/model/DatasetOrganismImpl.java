package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "species",
    "strain"
})
public class DatasetOrganismImpl implements DatasetOrganism {
  @JsonProperty(JsonField.SPECIES)
  private String species;

  @JsonProperty(JsonField.STRAIN)
  private String strain;

  @JsonProperty(JsonField.SPECIES)
  public String getSpecies() {
    return this.species;
  }

  @JsonProperty(JsonField.SPECIES)
  public void setSpecies(String species) {
    this.species = species;
  }

  @JsonProperty(JsonField.STRAIN)
  public String getStrain() {
    return this.strain;
  }

  @JsonProperty(JsonField.STRAIN)
  public void setStrain(String strain) {
    this.strain = strain;
  }
}
