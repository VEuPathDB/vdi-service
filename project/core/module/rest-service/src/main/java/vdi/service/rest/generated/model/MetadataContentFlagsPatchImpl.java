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
public class MetadataContentFlagsPatchImpl implements MetadataContentFlagsPatch {
  @JsonProperty(JsonField.HAS_DATASET_CHARACTERISTICS)
  private OptionalBooleanPatch hasDatasetCharacteristics;

  @JsonProperty(JsonField.HAS_DATA_DISCLAIMER)
  private OptionalBooleanPatch hasDataDisclaimer;

  @JsonProperty(JsonField.HAS_DATASET_SOURCES)
  private OptionalBooleanPatch hasDatasetSources;

  @JsonProperty(JsonField.HAS_ORGANISM_DATA)
  private OptionalBooleanPatch hasOrganismData;

  @JsonProperty(JsonField.HAS_PUBLICATIONS)
  private OptionalBooleanPatch hasPublications;

  @JsonProperty(JsonField.HAS_DATASET_CHARACTERISTICS)
  public OptionalBooleanPatch getHasDatasetCharacteristics() {
    return this.hasDatasetCharacteristics;
  }

  @JsonProperty(JsonField.HAS_DATASET_CHARACTERISTICS)
  public void setHasDatasetCharacteristics(OptionalBooleanPatch hasDatasetCharacteristics) {
    this.hasDatasetCharacteristics = hasDatasetCharacteristics;
  }

  @JsonProperty(JsonField.HAS_DATA_DISCLAIMER)
  public OptionalBooleanPatch getHasDataDisclaimer() {
    return this.hasDataDisclaimer;
  }

  @JsonProperty(JsonField.HAS_DATA_DISCLAIMER)
  public void setHasDataDisclaimer(OptionalBooleanPatch hasDataDisclaimer) {
    this.hasDataDisclaimer = hasDataDisclaimer;
  }

  @JsonProperty(JsonField.HAS_DATASET_SOURCES)
  public OptionalBooleanPatch getHasDatasetSources() {
    return this.hasDatasetSources;
  }

  @JsonProperty(JsonField.HAS_DATASET_SOURCES)
  public void setHasDatasetSources(OptionalBooleanPatch hasDatasetSources) {
    this.hasDatasetSources = hasDatasetSources;
  }

  @JsonProperty(JsonField.HAS_ORGANISM_DATA)
  public OptionalBooleanPatch getHasOrganismData() {
    return this.hasOrganismData;
  }

  @JsonProperty(JsonField.HAS_ORGANISM_DATA)
  public void setHasOrganismData(OptionalBooleanPatch hasOrganismData) {
    this.hasOrganismData = hasOrganismData;
  }

  @JsonProperty(JsonField.HAS_PUBLICATIONS)
  public OptionalBooleanPatch getHasPublications() {
    return this.hasPublications;
  }

  @JsonProperty(JsonField.HAS_PUBLICATIONS)
  public void setHasPublications(OptionalBooleanPatch hasPublications) {
    this.hasPublications = hasPublications;
  }
}
