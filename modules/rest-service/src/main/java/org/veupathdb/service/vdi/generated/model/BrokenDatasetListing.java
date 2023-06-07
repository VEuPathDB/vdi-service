package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = BrokenDatasetListingImpl.class
)
public interface BrokenDatasetListing {
  @JsonProperty("details")
  List<BrokenDatasetDetails> getDetails();

  @JsonProperty("details")
  void setDetails(List<BrokenDatasetDetails> details);

  @JsonProperty("ids")
  List<String> getIds();

  @JsonProperty("ids")
  void setIds(List<String> ids);
}
