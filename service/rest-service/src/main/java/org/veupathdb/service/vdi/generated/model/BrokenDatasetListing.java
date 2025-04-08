package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = BrokenDatasetListingImpl.class
)
public interface BrokenDatasetListing {
  @JsonProperty(JsonField.DETAILS)
  List<BrokenDatasetDetails> getDetails();

  @JsonProperty(JsonField.DETAILS)
  void setDetails(List<BrokenDatasetDetails> details);

  @JsonProperty(JsonField.IDS)
  List<String> getIds();

  @JsonProperty(JsonField.IDS)
  void setIds(List<String> ids);
}
