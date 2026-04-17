package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = OrganismPatchImpl.class
)
public interface OrganismPatch {
  @JsonProperty("value")
  DatasetOrganism getValue();

  @JsonProperty("value")
  void setValue(DatasetOrganism value);
}
