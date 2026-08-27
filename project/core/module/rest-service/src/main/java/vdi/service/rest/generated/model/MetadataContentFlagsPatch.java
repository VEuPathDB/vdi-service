package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = MetadataContentFlagsPatchImpl.class
)
public interface MetadataContentFlagsPatch {
  @JsonProperty(JsonField.HAS_DATASET_CHARACTERISTICS)
  OptionalBooleanPatch getHasDatasetCharacteristics();

  @JsonProperty(JsonField.HAS_DATASET_CHARACTERISTICS)
  void setHasDatasetCharacteristics(OptionalBooleanPatch hasDatasetCharacteristics);

  @JsonProperty(JsonField.HAS_DATA_DISCLAIMER)
  OptionalBooleanPatch getHasDataDisclaimer();

  @JsonProperty(JsonField.HAS_DATA_DISCLAIMER)
  void setHasDataDisclaimer(OptionalBooleanPatch hasDataDisclaimer);

  @JsonProperty(JsonField.HAS_DATASET_SOURCES)
  OptionalBooleanPatch getHasDatasetSources();

  @JsonProperty(JsonField.HAS_DATASET_SOURCES)
  void setHasDatasetSources(OptionalBooleanPatch hasDatasetSources);

  @JsonProperty(JsonField.HAS_ORGANISM_DATA)
  OptionalBooleanPatch getHasOrganismData();

  @JsonProperty(JsonField.HAS_ORGANISM_DATA)
  void setHasOrganismData(OptionalBooleanPatch hasOrganismData);

  @JsonProperty(JsonField.HAS_PUBLICATIONS)
  OptionalBooleanPatch getHasPublications();

  @JsonProperty(JsonField.HAS_PUBLICATIONS)
  void setHasPublications(OptionalBooleanPatch hasPublications);
}
