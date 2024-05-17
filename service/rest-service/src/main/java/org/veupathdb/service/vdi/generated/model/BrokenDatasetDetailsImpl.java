package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetId",
    "owner",
    "datasetType",
    "statuses"
})
public class BrokenDatasetDetailsImpl implements BrokenDatasetDetails {
  @JsonProperty("datasetId")
  private String datasetId;

  @JsonProperty("owner")
  private Long owner;

  @JsonProperty("datasetType")
  private DatasetTypeInfo datasetType;

  @JsonProperty("statuses")
  private List<BrokenDatasetStatusEntry> statuses;

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

  @JsonProperty("datasetType")
  public DatasetTypeInfo getDatasetType() {
    return this.datasetType;
  }

  @JsonProperty("datasetType")
  public void setDatasetType(DatasetTypeInfo datasetType) {
    this.datasetType = datasetType;
  }

  @JsonProperty("statuses")
  public List<BrokenDatasetStatusEntry> getStatuses() {
    return this.statuses;
  }

  @JsonProperty("statuses")
  public void setStatuses(List<BrokenDatasetStatusEntry> statuses) {
    this.statuses = statuses;
  }
}
