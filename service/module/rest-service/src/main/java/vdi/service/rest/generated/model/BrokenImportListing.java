package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(
    as = BrokenImportListingImpl.class
)
public interface BrokenImportListing {
  @JsonProperty(JsonField.META)
  BrokenImportListingMeta getMeta();

  @JsonProperty(JsonField.META)
  void setMeta(BrokenImportListingMeta meta);

  @JsonProperty(JsonField.RESULTS)
  List<BrokenImportDetails> getResults();

  @JsonProperty(JsonField.RESULTS)
  void setResults(List<BrokenImportDetails> results);
}
