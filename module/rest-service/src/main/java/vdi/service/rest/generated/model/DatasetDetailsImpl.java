package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "summary",
    "origin",
    "projectIds",
    "dependencies",
    "category",
    "contacts",
    "description",
    "hyperlinks",
    "organisms",
    "publications",
    "shortName",
    "shortAttribution",
    "properties",
    "datasetId",
    "owner",
    "importMessages",
    "shares",
    "datasetType",
    "visibility",
    "status",
    "created",
    "sourceUrl",
    "originalId",
    "revisionHistory"
})
public class DatasetDetailsImpl implements DatasetDetails {
  @JsonProperty(JsonField.NAME)
  private String name;

  @JsonProperty(JsonField.SUMMARY)
  private String summary;

  @JsonProperty(JsonField.ORIGIN)
  private String origin;

  @JsonProperty(JsonField.PROJECT_IDS)
  private List<String> projectIds;

  @JsonProperty(JsonField.DEPENDENCIES)
  private List<DatasetDependency> dependencies;

  @JsonProperty(JsonField.CATEGORY)
  private String category;

  @JsonProperty(JsonField.CONTACTS)
  private List<DatasetContact> contacts;

  @JsonProperty(JsonField.DESCRIPTION)
  private String description;

  @JsonProperty(JsonField.HYPERLINKS)
  private List<DatasetHyperlink> hyperlinks;

  @JsonProperty(JsonField.ORGANISMS)
  private List<String> organisms;

  @JsonProperty(JsonField.PUBLICATIONS)
  private List<DatasetPublication> publications;

  @JsonProperty(JsonField.SHORT_NAME)
  private String shortName;

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  private String shortAttribution;

  @JsonProperty(JsonField.PROPERTIES)
  private com.fasterxml.jackson.databind.node.ObjectNode properties;

  @JsonProperty(JsonField.DATASET_ID)
  private String datasetId;

  @JsonProperty(JsonField.OWNER)
  private DatasetOwner owner;

  @JsonProperty(JsonField.IMPORT_MESSAGES)
  private List<String> importMessages;

  @JsonProperty(JsonField.SHARES)
  private List<ShareOffer> shares;

  @JsonProperty(JsonField.DATASET_TYPE)
  private DatasetTypeOutput datasetType;

  @JsonProperty(JsonField.VISIBILITY)
  private DatasetVisibility visibility;

  @JsonProperty(JsonField.STATUS)
  private DatasetStatusInfo status;

  @JsonProperty(JsonField.CREATED)
  private OffsetDateTime created;

  @JsonProperty(JsonField.SOURCE_URL)
  private String sourceUrl;

  @JsonProperty(JsonField.ORIGINAL_ID)
  private String originalId;

  @JsonProperty(JsonField.REVISION_HISTORY)
  private List<DatasetRevision> revisionHistory;

  @JsonProperty(JsonField.NAME)
  public String getName() {
    return this.name;
  }

  @JsonProperty(JsonField.NAME)
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty(JsonField.SUMMARY)
  public String getSummary() {
    return this.summary;
  }

  @JsonProperty(JsonField.SUMMARY)
  public void setSummary(String summary) {
    this.summary = summary;
  }

  @JsonProperty(JsonField.ORIGIN)
  public String getOrigin() {
    return this.origin;
  }

  @JsonProperty(JsonField.ORIGIN)
  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @JsonProperty(JsonField.PROJECT_IDS)
  public List<String> getProjectIds() {
    return this.projectIds;
  }

  @JsonProperty(JsonField.PROJECT_IDS)
  public void setProjectIds(List<String> projectIds) {
    this.projectIds = projectIds;
  }

  @JsonProperty(JsonField.DEPENDENCIES)
  public List<DatasetDependency> getDependencies() {
    return this.dependencies;
  }

  @JsonProperty(JsonField.DEPENDENCIES)
  public void setDependencies(List<DatasetDependency> dependencies) {
    this.dependencies = dependencies;
  }

  @JsonProperty(JsonField.CATEGORY)
  public String getCategory() {
    return this.category;
  }

  @JsonProperty(JsonField.CATEGORY)
  public void setCategory(String category) {
    this.category = category;
  }

  @JsonProperty(JsonField.CONTACTS)
  public List<DatasetContact> getContacts() {
    return this.contacts;
  }

  @JsonProperty(JsonField.CONTACTS)
  public void setContacts(List<DatasetContact> contacts) {
    this.contacts = contacts;
  }

  @JsonProperty(JsonField.DESCRIPTION)
  public String getDescription() {
    return this.description;
  }

  @JsonProperty(JsonField.DESCRIPTION)
  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty(JsonField.HYPERLINKS)
  public List<DatasetHyperlink> getHyperlinks() {
    return this.hyperlinks;
  }

  @JsonProperty(JsonField.HYPERLINKS)
  public void setHyperlinks(List<DatasetHyperlink> hyperlinks) {
    this.hyperlinks = hyperlinks;
  }

  @JsonProperty(JsonField.ORGANISMS)
  public List<String> getOrganisms() {
    return this.organisms;
  }

  @JsonProperty(JsonField.ORGANISMS)
  public void setOrganisms(List<String> organisms) {
    this.organisms = organisms;
  }

  @JsonProperty(JsonField.PUBLICATIONS)
  public List<DatasetPublication> getPublications() {
    return this.publications;
  }

  @JsonProperty(JsonField.PUBLICATIONS)
  public void setPublications(List<DatasetPublication> publications) {
    this.publications = publications;
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

  @JsonProperty(JsonField.PROPERTIES)
  public com.fasterxml.jackson.databind.node.ObjectNode getProperties() {
    return this.properties;
  }

  @JsonProperty(JsonField.PROPERTIES)
  public void setProperties(com.fasterxml.jackson.databind.node.ObjectNode properties) {
    this.properties = properties;
  }

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

  @JsonProperty(JsonField.IMPORT_MESSAGES)
  public List<String> getImportMessages() {
    return this.importMessages;
  }

  @JsonProperty(JsonField.IMPORT_MESSAGES)
  public void setImportMessages(List<String> importMessages) {
    this.importMessages = importMessages;
  }

  @JsonProperty(JsonField.SHARES)
  public List<ShareOffer> getShares() {
    return this.shares;
  }

  @JsonProperty(JsonField.SHARES)
  public void setShares(List<ShareOffer> shares) {
    this.shares = shares;
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

  @JsonProperty(JsonField.STATUS)
  public DatasetStatusInfo getStatus() {
    return this.status;
  }

  @JsonProperty(JsonField.STATUS)
  public void setStatus(DatasetStatusInfo status) {
    this.status = status;
  }

  @JsonProperty(JsonField.CREATED)
  public OffsetDateTime getCreated() {
    return this.created;
  }

  @JsonProperty(JsonField.CREATED)
  public void setCreated(OffsetDateTime created) {
    this.created = created;
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

  @JsonProperty(JsonField.REVISION_HISTORY)
  public List<DatasetRevision> getRevisionHistory() {
    return this.revisionHistory;
  }

  @JsonProperty(JsonField.REVISION_HISTORY)
  public void setRevisionHistory(List<DatasetRevision> revisionHistory) {
    this.revisionHistory = revisionHistory;
  }
}
