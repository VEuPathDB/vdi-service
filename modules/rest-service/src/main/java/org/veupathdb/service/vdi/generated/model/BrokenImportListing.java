package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(
    as = BrokenImportListingImpl.class
)
public interface BrokenImportListing {
  @JsonProperty("meta")
  BrokenImportListingMeta getMeta();

  @JsonProperty("meta")
  void setMeta(BrokenImportListingMeta meta);

  @JsonProperty("results")
  List<BrokenImportDetails> getResults();

  @JsonProperty("results")
  void setResults(List<BrokenImportDetails> results);
}
