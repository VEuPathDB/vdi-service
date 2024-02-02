package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "details",
    "ids"
})
public class BrokenDatasetListingImpl implements BrokenDatasetListing {
  @JsonProperty("details")
  private List<BrokenDatasetDetails> details;

  @JsonProperty("ids")
  private List<String> ids;

  @JsonProperty("details")
  public List<BrokenDatasetDetails> getDetails() {
    return this.details;
  }

  @JsonProperty("details")
  public void setDetails(List<BrokenDatasetDetails> details) {
    this.details = details;
  }

  @JsonProperty("ids")
  public List<String> getIds() {
    return this.ids;
  }

  @JsonProperty("ids")
  public void setIds(List<String> ids) {
    this.ids = ids;
  }
}
