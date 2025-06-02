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
public class AllDatasetsListResponseImpl implements AllDatasetsListResponse {
  @JsonProperty(JsonField.META)
  private AllDatasetsListMeta meta;

  @JsonProperty(JsonField.RESULTS)
  private List<AllDatasetsListEntry> results;

  @JsonProperty(JsonField.META)
  public AllDatasetsListMeta getMeta() {
    return this.meta;
  }

  @JsonProperty(JsonField.META)
  public void setMeta(AllDatasetsListMeta meta) {
    this.meta = meta;
  }

  @JsonProperty(JsonField.RESULTS)
  public List<AllDatasetsListEntry> getResults() {
    return this.results;
  }

  @JsonProperty(JsonField.RESULTS)
  public void setResults(List<AllDatasetsListEntry> results) {
    this.results = results;
  }
}
