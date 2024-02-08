package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;
import java.util.List;

@JsonDeserialize(
    as = AllDatasetsListEntryImpl.class
)
public interface AllDatasetsListEntry {
  @JsonProperty("datasetId")
  String getDatasetId();

  @JsonProperty("datasetId")
  void setDatasetId(String datasetId);

  @JsonProperty("owner")
  Long getOwner();

  @JsonProperty("owner")
  void setOwner(Long owner);

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

  @JsonProperty("projectIds")
  List<String> getProjectIds();

  @JsonProperty("projectIds")
  void setProjectIds(List<String> projectIds);

  @JsonProperty("status")
  DatasetStatusInfo getStatus();

  @JsonProperty("status")
  void setStatus(DatasetStatusInfo status);

  @JsonProperty("created")
  OffsetDateTime getCreated();

  @JsonProperty("created")
  void setCreated(OffsetDateTime created);

  @JsonProperty("isDeleted")
  Boolean getIsDeleted();

  @JsonProperty("isDeleted")
  void setIsDeleted(Boolean isDeleted);
}
