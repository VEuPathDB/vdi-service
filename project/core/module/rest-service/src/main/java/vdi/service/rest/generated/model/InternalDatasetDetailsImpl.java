package vdi.service.rest.generated.model;

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
    "installTargets",
    "status",
    "importMessages",
    "visibility",
    "shortName",
    "shortAttribution",
    "summary",
    "description",
    "sourceUrl",
    "syncControl",
    "uploadFiles",
    "installFiles"
})
public class InternalDatasetDetailsImpl implements InternalDatasetDetails {
  @JsonProperty(JsonField.DATASET_TYPE)
  private DatasetTypeOutput datasetType;

  @JsonProperty(JsonField.OWNER)
  private Long owner;

  @JsonProperty(JsonField.IS_DELETED)
  private Boolean isDeleted;

  @JsonProperty(JsonField.ORIGIN)
  private String origin;

  @JsonProperty(JsonField.CREATED)
  private OffsetDateTime created;

  @JsonProperty(JsonField.INSERTED)
  private OffsetDateTime inserted;

  @JsonProperty(JsonField.NAME)
  private String name;

  @JsonProperty(JsonField.INSTALL_TARGETS)
  private List<String> installTargets;

  @JsonProperty(JsonField.STATUS)
  private String status;

  @JsonProperty(JsonField.IMPORT_MESSAGES)
  private List<String> importMessages;

  @JsonProperty(JsonField.VISIBILITY)
  private DatasetVisibility visibility;

  @JsonProperty(JsonField.SHORT_NAME)
  private String shortName;

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  private String shortAttribution;

  @JsonProperty(JsonField.SUMMARY)
  private String summary;

  @JsonProperty(JsonField.DESCRIPTION)
  private String description;

  @JsonProperty(JsonField.SOURCE_URL)
  private String sourceUrl;

  @JsonProperty(JsonField.SYNC_CONTROL)
  private SyncControlRecord syncControl;

  @JsonProperty(JsonField.UPLOAD_FILES)
  private List<String> uploadFiles;

  @JsonProperty(JsonField.INSTALL_FILES)
  private List<String> installFiles;

  @JsonProperty(JsonField.DATASET_TYPE)
  public DatasetTypeOutput getDatasetType() {
    return this.datasetType;
  }

  @JsonProperty(JsonField.DATASET_TYPE)
  public void setDatasetType(DatasetTypeOutput datasetType) {
    this.datasetType = datasetType;
  }

  @JsonProperty(JsonField.OWNER)
  public Long getOwner() {
    return this.owner;
  }

  @JsonProperty(JsonField.OWNER)
  public void setOwner(Long owner) {
    this.owner = owner;
  }

  @JsonProperty(JsonField.IS_DELETED)
  public Boolean getIsDeleted() {
    return this.isDeleted;
  }

  @JsonProperty(JsonField.IS_DELETED)
  public void setIsDeleted(Boolean isDeleted) {
    this.isDeleted = isDeleted;
  }

  @JsonProperty(JsonField.ORIGIN)
  public String getOrigin() {
    return this.origin;
  }

  @JsonProperty(JsonField.ORIGIN)
  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @JsonProperty(JsonField.CREATED)
  public OffsetDateTime getCreated() {
    return this.created;
  }

  @JsonProperty(JsonField.CREATED)
  public void setCreated(OffsetDateTime created) {
    this.created = created;
  }

  @JsonProperty(JsonField.INSERTED)
  public OffsetDateTime getInserted() {
    return this.inserted;
  }

  @JsonProperty(JsonField.INSERTED)
  public void setInserted(OffsetDateTime inserted) {
    this.inserted = inserted;
  }

  @JsonProperty(JsonField.NAME)
  public String getName() {
    return this.name;
  }

  @JsonProperty(JsonField.NAME)
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty(JsonField.INSTALL_TARGETS)
  public List<String> getInstallTargets() {
    return this.installTargets;
  }

  @JsonProperty(JsonField.INSTALL_TARGETS)
  public void setInstallTargets(List<String> installTargets) {
    this.installTargets = installTargets;
  }

  @JsonProperty(JsonField.STATUS)
  public String getStatus() {
    return this.status;
  }

  @JsonProperty(JsonField.STATUS)
  public void setStatus(String status) {
    this.status = status;
  }

  @JsonProperty(JsonField.IMPORT_MESSAGES)
  public List<String> getImportMessages() {
    return this.importMessages;
  }

  @JsonProperty(JsonField.IMPORT_MESSAGES)
  public void setImportMessages(List<String> importMessages) {
    this.importMessages = importMessages;
  }

  @JsonProperty(JsonField.VISIBILITY)
  public DatasetVisibility getVisibility() {
    return this.visibility;
  }

  @JsonProperty(JsonField.VISIBILITY)
  public void setVisibility(DatasetVisibility visibility) {
    this.visibility = visibility;
  }

  @JsonProperty(JsonField.SHORT_NAME)
  public String getShortName() {
    return this.shortName;
  }

  @JsonProperty(JsonField.SHORT_NAME)
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  public String getShortAttribution() {
    return this.shortAttribution;
  }

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  public void setShortAttribution(String shortAttribution) {
    this.shortAttribution = shortAttribution;
  }

  @JsonProperty(JsonField.SUMMARY)
  public String getSummary() {
    return this.summary;
  }

  @JsonProperty(JsonField.SUMMARY)
  public void setSummary(String summary) {
    this.summary = summary;
  }

  @JsonProperty(JsonField.DESCRIPTION)
  public String getDescription() {
    return this.description;
  }

  @JsonProperty(JsonField.DESCRIPTION)
  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty(JsonField.SOURCE_URL)
  public String getSourceUrl() {
    return this.sourceUrl;
  }

  @JsonProperty(JsonField.SOURCE_URL)
  public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  @JsonProperty(JsonField.SYNC_CONTROL)
  public SyncControlRecord getSyncControl() {
    return this.syncControl;
  }

  @JsonProperty(JsonField.SYNC_CONTROL)
  public void setSyncControl(SyncControlRecord syncControl) {
    this.syncControl = syncControl;
  }

  @JsonProperty(JsonField.UPLOAD_FILES)
  public List<String> getUploadFiles() {
    return this.uploadFiles;
  }

  @JsonProperty(JsonField.UPLOAD_FILES)
  public void setUploadFiles(List<String> uploadFiles) {
    this.uploadFiles = uploadFiles;
  }

  @JsonProperty(JsonField.INSTALL_FILES)
  public List<String> getInstallFiles() {
    return this.installFiles;
  }

  @JsonProperty(JsonField.INSTALL_FILES)
  public void setInstallFiles(List<String> installFiles) {
    this.installFiles = installFiles;
  }
}
