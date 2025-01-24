package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;
import java.util.List;

@JsonDeserialize(
    as = DatasetListEntryImpl.class
)
public interface DatasetListEntry {
  @JsonProperty("datasetId")
  String getDatasetId();

  @JsonProperty("datasetId")
  void setDatasetId(String datasetId);

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

  @JsonProperty("shortName")
  String getShortName();

  @JsonProperty("shortName")
  void setShortName(String shortName);

  @JsonProperty("shortAttribution")
  String getShortAttribution();

  @JsonProperty("shortAttribution")
  void setShortAttribution(String shortAttribution);

  @JsonProperty("category")
  String getCategory();

  @JsonProperty("category")
  void setCategory(String category);

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

  @JsonProperty("projectIds")
  List<String> getProjectIds();

  @JsonProperty("projectIds")
  void setProjectIds(List<String> projectIds);

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
  OffsetDateTime getCreated();

  @JsonProperty("created")
  void setCreated(OffsetDateTime created);
}
