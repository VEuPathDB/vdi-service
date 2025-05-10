package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = AllDatasetsListResponseImpl.class
)
public interface AllDatasetsListResponse {
  @JsonProperty("meta")
  AllDatasetsListMeta getMeta();

  @JsonProperty("meta")
  void setMeta(AllDatasetsListMeta meta);

  @JsonProperty("results")
  List<AllDatasetsListEntry> getResults();

  @JsonProperty("results")
  void setResults(List<AllDatasetsListEntry> results);
}
