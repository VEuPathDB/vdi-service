package vdi.service.rest.generated.model;

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
  @JsonProperty(JsonField.META)
  private BrokenImportListingMeta meta;

  @JsonProperty(JsonField.RESULTS)
  private List<BrokenImportDetails> results;

  @JsonProperty(JsonField.META)
  public BrokenImportListingMeta getMeta() {
    return this.meta;
  }

  @JsonProperty(JsonField.META)
  public void setMeta(BrokenImportListingMeta meta) {
    this.meta = meta;
  }

  @JsonProperty(JsonField.RESULTS)
  public List<BrokenImportDetails> getResults() {
    return this.results;
  }

  @JsonProperty(JsonField.RESULTS)
  public void setResults(List<BrokenImportDetails> results) {
    this.results = results;
  }
}
