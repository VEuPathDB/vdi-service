package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = LinkedDatasetPatchImpl.class
)
public interface LinkedDatasetPatch {
  @JsonProperty("value")
  List<LinkedDataset> getValue();

  @JsonProperty("value")
  void setValue(List<LinkedDataset> value);
}
