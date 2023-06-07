package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetID",
    "owner",
    "datasetType",
    "name",
    "projectsIDs",
    "status"
})
public class BrokenDatasetDetailsImpl implements BrokenDatasetDetails {
  @JsonProperty("datasetID")
  private String datasetID;

  @JsonProperty("owner")
  private Long owner;

  @JsonProperty("datasetType")
  private DatasetTypeInfo datasetType;

  @JsonProperty("name")
  private String name;

  @JsonProperty("projectsIDs")
  private List<String> projectsIDs;

  @JsonProperty("status")
  private DatasetStatusInfo status;

  @JsonProperty("datasetID")
  public String getDatasetID() {
    return this.datasetID;
  }

  @JsonProperty("datasetID")
  public void setDatasetID(String datasetID) {
    this.datasetID = datasetID;
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

  @JsonProperty("name")
  public String getName() {
    return this.name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("projectsIDs")
  public List<String> getProjectsIDs() {
    return this.projectsIDs;
  }

  @JsonProperty("projectsIDs")
  public void setProjectsIDs(List<String> projectsIDs) {
    this.projectsIDs = projectsIDs;
  }

  @JsonProperty("status")
  public DatasetStatusInfo getStatus() {
    return this.status;
  }

  @JsonProperty("status")
  public void setStatus(DatasetStatusInfo status) {
    this.status = status;
  }
}
