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
public class AllDatasetsListResponseImpl implements AllDatasetsListResponse {
  @JsonProperty("meta")
  private AllDatasetsListMeta meta;

  @JsonProperty("results")
  private List<AllDatasetsListEntry> results;

  @JsonProperty("meta")
  public AllDatasetsListMeta getMeta() {
    return this.meta;
  }

  @JsonProperty("meta")
  public void setMeta(AllDatasetsListMeta meta) {
    this.meta = meta;
  }

  @JsonProperty("results")
  public List<AllDatasetsListEntry> getResults() {
    return this.results;
  }

  @JsonProperty("results")
  public void setResults(List<AllDatasetsListEntry> results) {
    this.results = results;
  }
}
