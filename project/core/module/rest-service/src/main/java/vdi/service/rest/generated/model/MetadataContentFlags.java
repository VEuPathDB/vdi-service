package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = MetadataContentFlagsImpl.class
)
public interface MetadataContentFlags {
  @JsonProperty(JsonField.HAS_DATASET_CHARACTERISTICS)
  Boolean getHasDatasetCharacteristics();

  @JsonProperty(JsonField.HAS_DATASET_CHARACTERISTICS)
  void setHasDatasetCharacteristics(Boolean hasDatasetCharacteristics);

  @JsonProperty(JsonField.HAS_DATA_DISCLAIMER)
  Boolean getHasDataDisclaimer();

  @JsonProperty(JsonField.HAS_DATA_DISCLAIMER)
  void setHasDataDisclaimer(Boolean hasDataDisclaimer);

  @JsonProperty(JsonField.HAS_DATASET_SOURCES)
  Boolean getHasDatasetSources();

  @JsonProperty(JsonField.HAS_DATASET_SOURCES)
  void setHasDatasetSources(Boolean hasDatasetSources);

  @JsonProperty(JsonField.HAS_ORGANISM_DATA)
  Boolean getHasOrganismData();

  @JsonProperty(JsonField.HAS_ORGANISM_DATA)
  void setHasOrganismData(Boolean hasOrganismData);

  @JsonProperty(JsonField.HAS_PUBLICATIONS)
  Boolean getHasPublications();

  @JsonProperty(JsonField.HAS_PUBLICATIONS)
  void setHasPublications(Boolean hasPublications);
}
