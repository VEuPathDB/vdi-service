package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetType",
    "owner",
    "isDeleted",
    "origin",
    "created",
    "inserted",
    "name",
    "summary",
    "description",
    "visibility",
    "sourceUrl",
    "projectIds",
    "status",
    "syncControl",
    "uploadFiles",
    "installFiles",
    "importMessages"
})
public class InternalDatasetDetailsImpl implements InternalDatasetDetails {
  @JsonProperty("datasetType")
  private DatasetTypeInfo datasetType;

  @JsonProperty("owner")
  private Long owner;

  @JsonProperty("isDeleted")
  private Boolean isDeleted;

  @JsonProperty("origin")
  private String origin;

  @JsonProperty("created")
  private OffsetDateTime created;

  @JsonProperty("inserted")
  private OffsetDateTime inserted;

  @JsonProperty("name")
  private String name;

  @JsonProperty("summary")
  private String summary;

  @JsonProperty("description")
  private String description;

  @JsonProperty("visibility")
  private DatasetVisibility visibility;

  @JsonProperty("sourceUrl")
  private String sourceUrl;

  @JsonProperty("projectIds")
  private List<String> projectIds;

  @JsonProperty("status")
  private String status;

  @JsonProperty("syncControl")
  private SyncControlRecord syncControl;

  @JsonProperty("uploadFiles")
  private List<String> uploadFiles;

  @JsonProperty("installFiles")
  private List<String> installFiles;

  @JsonProperty("importMessages")
  private List<String> importMessages;

  @JsonProperty("datasetType")
  public DatasetTypeInfo getDatasetType() {
    return this.datasetType;
  }

  @JsonProperty("datasetType")
  public void setDatasetType(DatasetTypeInfo datasetType) {
    this.datasetType = datasetType;
  }

  @JsonProperty("owner")
  public Long getOwner() {
    return this.owner;
  }

  @JsonProperty("owner")
  public void setOwner(Long owner) {
    this.owner = owner;
  }

  @JsonProperty("isDeleted")
  public Boolean getIsDeleted() {
    return this.isDeleted;
  }

  @JsonProperty("isDeleted")
  public void setIsDeleted(Boolean isDeleted) {
    this.isDeleted = isDeleted;
  }

  @JsonProperty("origin")
  public String getOrigin() {
    return this.origin;
  }

  @JsonProperty("origin")
  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @JsonProperty("created")
  public OffsetDateTime getCreated() {
    return this.created;
  }

  @JsonProperty("created")
  public void setCreated(OffsetDateTime created) {
    this.created = created;
  }

  @JsonProperty("inserted")
  public OffsetDateTime getInserted() {
    return this.inserted;
  }

  @JsonProperty("inserted")
  public void setInserted(OffsetDateTime inserted) {
    this.inserted = inserted;
  }

  @JsonProperty("name")
  public String getName() {
    return this.name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("summary")
  public String getSummary() {
    return this.summary;
  }

  @JsonProperty("summary")
  public void setSummary(String summary) {
    this.summary = summary;
  }

  @JsonProperty("description")
  public String getDescription() {
    return this.description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty("visibility")
  public DatasetVisibility getVisibility() {
    return this.visibility;
  }

  @JsonProperty("visibility")
  public void setVisibility(DatasetVisibility visibility) {
    this.visibility = visibility;
  }

  @JsonProperty("sourceUrl")
  public String getSourceUrl() {
    return this.sourceUrl;
  }

  @JsonProperty("sourceUrl")
  public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  @JsonProperty("projectIds")
  public List<String> getProjectIds() {
    return this.projectIds;
  }

  @JsonProperty("projectIds")
  public void setProjectIds(List<String> projectIds) {
    this.projectIds = projectIds;
  }

  @JsonProperty("status")
  public String getStatus() {
    return this.status;
  }

  @JsonProperty("status")
  public void setStatus(String status) {
    this.status = status;
  }

  @JsonProperty("syncControl")
  public SyncControlRecord getSyncControl() {
    return this.syncControl;
  }

  @JsonProperty("syncControl")
  public void setSyncControl(SyncControlRecord syncControl) {
    this.syncControl = syncControl;
  }

  @JsonProperty("uploadFiles")
  public List<String> getUploadFiles() {
    return this.uploadFiles;
  }

  @JsonProperty("uploadFiles")
  public void setUploadFiles(List<String> uploadFiles) {
    this.uploadFiles = uploadFiles;
  }

  @JsonProperty("installFiles")
  public List<String> getInstallFiles() {
    return this.installFiles;
  }

  @JsonProperty("installFiles")
  public void setInstallFiles(List<String> installFiles) {
    this.installFiles = installFiles;
  }

  @JsonProperty("importMessages")
  public List<String> getImportMessages() {
    return this.importMessages;
  }

  @JsonProperty("importMessages")
  public void setImportMessages(List<String> importMessages) {
    this.importMessages = importMessages;
  }
}
