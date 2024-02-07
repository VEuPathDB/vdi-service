package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.OffsetDateTime;
import java.util.List;

@JsonDeserialize(
    as = InternalDatasetDetailsImpl.class
)
public interface InternalDatasetDetails {
  @JsonProperty("datasetType")
  DatasetTypeInfo getDatasetType();

  @JsonProperty("datasetType")
  void setDatasetType(DatasetTypeInfo datasetType);

  @JsonProperty("owner")
  Long getOwner();

  @JsonProperty("owner")
  void setOwner(Long owner);

  @JsonProperty("isDeleted")
  Boolean getIsDeleted();

  @JsonProperty("isDeleted")
  void setIsDeleted(Boolean isDeleted);

  @JsonProperty("origin")
  String getOrigin();

  @JsonProperty("origin")
  void setOrigin(String origin);

  @JsonProperty("created")
  OffsetDateTime getCreated();

  @JsonProperty("created")
  void setCreated(OffsetDateTime created);

  @JsonProperty("inserted")
  OffsetDateTime getInserted();

  @JsonProperty("inserted")
  void setInserted(OffsetDateTime inserted);

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

  @JsonProperty("visibility")
  DatasetVisibility getVisibility();

  @JsonProperty("visibility")
  void setVisibility(DatasetVisibility visibility);

  @JsonProperty("sourceUrl")
  String getSourceUrl();

  @JsonProperty("sourceUrl")
  void setSourceUrl(String sourceUrl);

  @JsonProperty("projectIds")
  List<String> getProjectIds();

  @JsonProperty("projectIds")
  void setProjectIds(List<String> projectIds);

  @JsonProperty("status")
  String getStatus();

  @JsonProperty("status")
  void setStatus(String status);

  @JsonProperty("syncControl")
  SyncControlRecord getSyncControl();

  @JsonProperty("syncControl")
  void setSyncControl(SyncControlRecord syncControl);

  @JsonProperty("uploadFiles")
  List<String> getUploadFiles();

  @JsonProperty("uploadFiles")
  void setUploadFiles(List<String> uploadFiles);

  @JsonProperty("installFiles")
  List<String> getInstallFiles();

  @JsonProperty("installFiles")
  void setInstallFiles(List<String> installFiles);

  @JsonProperty("importMessages")
  List<String> getImportMessages();

  @JsonProperty("importMessages")
  void setImportMessages(List<String> importMessages);
}
