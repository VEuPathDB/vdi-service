package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;
import java.util.List;

@JsonDeserialize(
    as = DatasetListEntryImpl.class
)
public interface DatasetListEntry {
  @JsonProperty("datasetID")
  String getDatasetID();

  @JsonProperty("datasetID")
  void setDatasetID(String datasetID);

  @JsonProperty("owner")
  DatasetOwner getOwner();

  @JsonProperty("owner")
  void setOwner(DatasetOwner owner);

  @JsonProperty("datasetType")
  DatasetTypeInfo getDatasetType();

  @JsonProperty("datasetType")
  void setDatasetType(DatasetTypeInfo datasetType);

  @JsonProperty("visibility")
  DatasetVisibility getVisibility();

  @JsonProperty("visibility")
  void setVisibility(DatasetVisibility visibility);

  @JsonProperty("name")
  String getName();

  @JsonProperty("name")
  void setName(String name);

  @JsonProperty("summary")
  String getSummary();

  @JsonProperty("summary")
  void setSummary(String summary);

  @JsonProperty("description")
  String getDescription();

  @JsonProperty("description")
  void setDescription(String description);

  @JsonProperty("sourceUrl")
  String getSourceUrl();

  @JsonProperty("sourceUrl")
  void setSourceUrl(String sourceUrl);

  @JsonProperty("origin")
  String getOrigin();

  @JsonProperty("origin")
  void setOrigin(String origin);

  @JsonProperty("projectIDs")
  List<String> getProjectIDs();

  @JsonProperty("projectIDs")
  void setProjectIDs(List<String> projectIDs);

  @JsonProperty("status")
  DatasetStatusInfo getStatus();

  @JsonProperty("status")
  void setStatus(DatasetStatusInfo status);

  @JsonProperty("shares")
  List<DatasetListShareUser> getShares();

  @JsonProperty("shares")
  void setShares(List<DatasetListShareUser> shares);

  @JsonProperty("fileCount")
  Integer getFileCount();

  @JsonProperty("fileCount")
  void setFileCount(Integer fileCount);

  @JsonProperty("fileSizeTotal")
  Long getFileSizeTotal();

  @JsonProperty("fileSizeTotal")
  void setFileSizeTotal(Long fileSizeTotal);

  @JsonProperty("created")
  Date getCreated();

  @JsonProperty("created")
  void setCreated(Date created);
}
