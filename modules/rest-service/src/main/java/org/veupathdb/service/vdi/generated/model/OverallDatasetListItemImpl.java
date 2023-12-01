package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetId",
    "owner"
})
public class OverallDatasetListItemImpl implements OverallDatasetListItem {
  @JsonProperty("datasetId")
  private String datasetId;

  @JsonProperty("owner")
  private Long owner;

  @JsonProperty("datasetId")
  public String getDatasetId() {
    return this.datasetId;
  }

  @JsonProperty("datasetId")
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  @JsonProperty("owner")
  public Long getOwner() {
    return this.owner;
  }

  @JsonProperty("owner")
  public void setOwner(Long owner) {
    this.owner = owner;
  }
}
