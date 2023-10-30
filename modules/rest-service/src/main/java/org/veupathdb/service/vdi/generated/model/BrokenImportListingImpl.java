package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "meta",
    "results"
})
public class BrokenImportListingImpl implements BrokenImportListing {
  @JsonProperty("meta")
  private BrokenImportListingMeta meta;

  @JsonProperty("results")
  private List<BrokenImportDetails> results;

  @JsonProperty("meta")
  public BrokenImportListingMeta getMeta() {
    return this.meta;
  }

  @JsonProperty("meta")
  public void setMeta(BrokenImportListingMeta meta) {
    this.meta = meta;
  }

  @JsonProperty("results")
  public List<BrokenImportDetails> getResults() {
    return this.results;
  }

  @JsonProperty("results")
  public void setResults(List<BrokenImportDetails> results) {
    this.results = results;
  }
}
