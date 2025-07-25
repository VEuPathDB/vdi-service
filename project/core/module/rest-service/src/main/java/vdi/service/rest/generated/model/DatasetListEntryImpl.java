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
    "datasetId",
    "owner",
    "datasetType",
    "visibility",
    "name",
    "origin",
    "installTargets",
    "status",
    "shares",
    "fileCount",
    "fileSizeTotal",
    "created",
    "shortName",
    "shortAttribution",
    "summary",
    "description",
    "sourceUrl",
    "originalId"
})
public class DatasetListEntryImpl implements DatasetListEntry {
  @JsonProperty(JsonField.DATASET_ID)
  private String datasetId;

  @JsonProperty(JsonField.OWNER)
  private DatasetOwner owner;

  @JsonProperty(JsonField.DATASET_TYPE)
  private DatasetTypeOutput datasetType;

  @JsonProperty(JsonField.VISIBILITY)
  private DatasetVisibility visibility;

  @JsonProperty(JsonField.NAME)
  private String name;

  @JsonProperty(JsonField.ORIGIN)
  private String origin;

  @JsonProperty(JsonField.INSTALL_TARGETS)
  private List<String> installTargets;

  @JsonProperty(JsonField.STATUS)
  private DatasetStatusInfo status;

  @JsonProperty(JsonField.SHARES)
  private List<DatasetListShareUser> shares;

  @JsonProperty(JsonField.FILE_COUNT)
  private Integer fileCount;

  @JsonProperty(JsonField.FILE_SIZE_TOTAL)
  private Long fileSizeTotal;

  @JsonProperty(JsonField.CREATED)
  private OffsetDateTime created;

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

  @JsonProperty(JsonField.ORIGINAL_ID)
  private String originalId;

  @JsonProperty(JsonField.DATASET_ID)
  public String getDatasetId() {
    return this.datasetId;
  }

  @JsonProperty(JsonField.DATASET_ID)
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  @JsonProperty(JsonField.OWNER)
  public DatasetOwner getOwner() {
    return this.owner;
  }

  @JsonProperty(JsonField.OWNER)
  public void setOwner(DatasetOwner owner) {
    this.owner = owner;
  }

  @JsonProperty(JsonField.DATASET_TYPE)
  public DatasetTypeOutput getDatasetType() {
    return this.datasetType;
  }

  @JsonProperty(JsonField.DATASET_TYPE)
  public void setDatasetType(DatasetTypeOutput datasetType) {
    this.datasetType = datasetType;
  }

  @JsonProperty(JsonField.VISIBILITY)
  public DatasetVisibility getVisibility() {
    return this.visibility;
  }

  @JsonProperty(JsonField.VISIBILITY)
  public void setVisibility(DatasetVisibility visibility) {
    this.visibility = visibility;
  }

  @JsonProperty(JsonField.NAME)
  public String getName() {
    return this.name;
  }

  @JsonProperty(JsonField.NAME)
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty(JsonField.ORIGIN)
  public String getOrigin() {
    return this.origin;
  }

  @JsonProperty(JsonField.ORIGIN)
  public void setOrigin(String origin) {
    this.origin = origin;
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
  public DatasetStatusInfo getStatus() {
    return this.status;
  }

  @JsonProperty(JsonField.STATUS)
  public void setStatus(DatasetStatusInfo status) {
    this.status = status;
  }

  @JsonProperty(JsonField.SHARES)
  public List<DatasetListShareUser> getShares() {
    return this.shares;
  }

  @JsonProperty(JsonField.SHARES)
  public void setShares(List<DatasetListShareUser> shares) {
    this.shares = shares;
  }

  @JsonProperty(JsonField.FILE_COUNT)
  public Integer getFileCount() {
    return this.fileCount;
  }

  @JsonProperty(JsonField.FILE_COUNT)
  public void setFileCount(Integer fileCount) {
    this.fileCount = fileCount;
  }

  @JsonProperty(JsonField.FILE_SIZE_TOTAL)
  public Long getFileSizeTotal() {
    return this.fileSizeTotal;
  }

  @JsonProperty(JsonField.FILE_SIZE_TOTAL)
  public void setFileSizeTotal(Long fileSizeTotal) {
    this.fileSizeTotal = fileSizeTotal;
  }

  @JsonProperty(JsonField.CREATED)
  public OffsetDateTime getCreated() {
    return this.created;
  }

  @JsonProperty(JsonField.CREATED)
  public void setCreated(OffsetDateTime created) {
    this.created = created;
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

  @JsonProperty(JsonField.ORIGINAL_ID)
  public String getOriginalId() {
    return this.originalId;
  }

  @JsonProperty(JsonField.ORIGINAL_ID)
  public void setOriginalId(String originalId) {
    this.originalId = originalId;
  }
}
