package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(
    as = AllDatasetsListResponseImpl.class
)
public interface AllDatasetsListResponse {
  @JsonProperty(JsonField.META)
  AllDatasetsListMeta getMeta();

  @JsonProperty(JsonField.META)
  void setMeta(AllDatasetsListMeta meta);

  @JsonProperty(JsonField.RESULTS)
  List<AllDatasetsListEntry> getResults();

  @JsonProperty(JsonField.RESULTS)
  void setResults(List<AllDatasetsListEntry> results);
}
