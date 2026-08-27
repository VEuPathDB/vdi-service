package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "hasDatasetCharacteristics",
    "hasDataDisclaimer",
    "hasDatasetSources",
    "hasOrganismData",
    "hasPublications"
})
public class MetadataContentFlagsImpl implements MetadataContentFlags {
  @JsonProperty(JsonField.HAS_DATASET_CHARACTERISTICS)
  private Boolean hasDatasetCharacteristics;

  @JsonProperty(JsonField.HAS_DATA_DISCLAIMER)
  private Boolean hasDataDisclaimer;

  @JsonProperty(JsonField.HAS_DATASET_SOURCES)
  private Boolean hasDatasetSources;

  @JsonProperty(JsonField.HAS_ORGANISM_DATA)
  private Boolean hasOrganismData;

  @JsonProperty(JsonField.HAS_PUBLICATIONS)
  private Boolean hasPublications;

  @JsonProperty(JsonField.HAS_DATASET_CHARACTERISTICS)
  public Boolean getHasDatasetCharacteristics() {
    return this.hasDatasetCharacteristics;
  }

  @JsonProperty(JsonField.HAS_DATASET_CHARACTERISTICS)
  public void setHasDatasetCharacteristics(Boolean hasDatasetCharacteristics) {
    this.hasDatasetCharacteristics = hasDatasetCharacteristics;
  }

  @JsonProperty(JsonField.HAS_DATA_DISCLAIMER)
  public Boolean getHasDataDisclaimer() {
    return this.hasDataDisclaimer;
  }

  @JsonProperty(JsonField.HAS_DATA_DISCLAIMER)
  public void setHasDataDisclaimer(Boolean hasDataDisclaimer) {
    this.hasDataDisclaimer = hasDataDisclaimer;
  }

  @JsonProperty(JsonField.HAS_DATASET_SOURCES)
  public Boolean getHasDatasetSources() {
    return this.hasDatasetSources;
  }

  @JsonProperty(JsonField.HAS_DATASET_SOURCES)
  public void setHasDatasetSources(Boolean hasDatasetSources) {
    this.hasDatasetSources = hasDatasetSources;
  }

  @JsonProperty(JsonField.HAS_ORGANISM_DATA)
  public Boolean getHasOrganismData() {
    return this.hasOrganismData;
  }

  @JsonProperty(JsonField.HAS_ORGANISM_DATA)
  public void setHasOrganismData(Boolean hasOrganismData) {
    this.hasOrganismData = hasOrganismData;
  }

  @JsonProperty(JsonField.HAS_PUBLICATIONS)
  public Boolean getHasPublications() {
    return this.hasPublications;
  }

  @JsonProperty(JsonField.HAS_PUBLICATIONS)
  public void setHasPublications(Boolean hasPublications) {
    this.hasPublications = hasPublications;
  }
}
