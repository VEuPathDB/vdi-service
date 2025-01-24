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
    "datasetId",
    "owner",
    "datasetType",
    "name",
    "shortName",
    "shortAttribution",
    "category",
    "summary",
    "description",
    "sourceUrl",
    "origin",
    "projectIds",
    "visibility",
    "importMessages",
    "status",
    "shares",
    "created",
    "dependencies",
    "publications",
    "hyperlinks",
    "taxonIds",
    "contacts"
})
public class DatasetDetailsImpl implements DatasetDetails {
  @JsonProperty("datasetId")
  private String datasetId;

  @JsonProperty("owner")
  private DatasetOwner owner;

  @JsonProperty("datasetType")
  private DatasetTypeInfo datasetType;

  @JsonProperty("name")
  private String name;

  @JsonProperty("shortName")
  private String shortName;

  @JsonProperty("shortAttribution")
  private String shortAttribution;

  @JsonProperty("category")
  private String category;

  @JsonProperty("summary")
  private String summary;

  @JsonProperty("description")
  private String description;

  @JsonProperty("sourceUrl")
  private String sourceUrl;

  @JsonProperty("origin")
  private String origin;

  @JsonProperty("projectIds")
  private List<String> projectIds;

  @JsonProperty("visibility")
  private DatasetVisibility visibility;

  @JsonProperty(
      value = "importMessages",
      defaultValue = "[\n"
              + "\n"
              + "]"
  )
  private List<String> importMessages;

  @JsonProperty("status")
  private DatasetStatusInfo status;

  @JsonProperty("shares")
  private List<ShareOffer> shares;

  @JsonProperty("created")
  private OffsetDateTime created;

  @JsonProperty("dependencies")
  private List<DatasetDependency> dependencies;

  @JsonProperty("publications")
  private List<DatasetPublication> publications;

  @JsonProperty("hyperlinks")
  private List<DatasetHyperlink> hyperlinks;

  @JsonProperty("taxonIds")
  private List<Long> taxonIds;

  @JsonProperty("contacts")
  private List<DatasetContact> contacts;

  @JsonProperty("datasetId")
  public String getDatasetId() {
    return this.datasetId;
  }

  @JsonProperty("datasetId")
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  @JsonProperty("owner")
  public DatasetOwner getOwner() {
    return this.owner;
  }

  @JsonProperty("owner")
  public void setOwner(DatasetOwner owner) {
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

  @JsonProperty("shortName")
  public String getShortName() {
    return this.shortName;
  }

  @JsonProperty("shortName")
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  @JsonProperty("shortAttribution")
  public String getShortAttribution() {
    return this.shortAttribution;
  }

  @JsonProperty("shortAttribution")
  public void setShortAttribution(String shortAttribution) {
    this.shortAttribution = shortAttribution;
  }

  @JsonProperty("category")
  public String getCategory() {
    return this.category;
  }

  @JsonProperty("category")
  public void setCategory(String category) {
    this.category = category;
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

  @JsonProperty("sourceUrl")
  public String getSourceUrl() {
    return this.sourceUrl;
  }

  @JsonProperty("sourceUrl")
  public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  @JsonProperty("origin")
  public String getOrigin() {
    return this.origin;
  }

  @JsonProperty("origin")
  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @JsonProperty("projectIds")
  public List<String> getProjectIds() {
    return this.projectIds;
  }

  @JsonProperty("projectIds")
  public void setProjectIds(List<String> projectIds) {
    this.projectIds = projectIds;
  }

  @JsonProperty("visibility")
  public DatasetVisibility getVisibility() {
    return this.visibility;
  }

  @JsonProperty("visibility")
  public void setVisibility(DatasetVisibility visibility) {
    this.visibility = visibility;
  }

  @JsonProperty(
      value = "importMessages",
      defaultValue = "[\n"
              + "\n"
              + "]"
  )
  public List<String> getImportMessages() {
    return this.importMessages;
  }

  @JsonProperty(
      value = "importMessages",
      defaultValue = "[\n"
              + "\n"
              + "]"
  )
  public void setImportMessages(List<String> importMessages) {
    this.importMessages = importMessages;
  }

  @JsonProperty("status")
  public DatasetStatusInfo getStatus() {
    return this.status;
  }

  @JsonProperty("status")
  public void setStatus(DatasetStatusInfo status) {
    this.status = status;
  }

  @JsonProperty("shares")
  public List<ShareOffer> getShares() {
    return this.shares;
  }

  @JsonProperty("shares")
  public void setShares(List<ShareOffer> shares) {
    this.shares = shares;
  }

  @JsonProperty("created")
  public OffsetDateTime getCreated() {
    return this.created;
  }

  @JsonProperty("created")
  public void setCreated(OffsetDateTime created) {
    this.created = created;
  }

  @JsonProperty("dependencies")
  public List<DatasetDependency> getDependencies() {
    return this.dependencies;
  }

  @JsonProperty("dependencies")
  public void setDependencies(List<DatasetDependency> dependencies) {
    this.dependencies = dependencies;
  }

  @JsonProperty("publications")
  public List<DatasetPublication> getPublications() {
    return this.publications;
  }

  @JsonProperty("publications")
  public void setPublications(List<DatasetPublication> publications) {
    this.publications = publications;
  }

  @JsonProperty("hyperlinks")
  public List<DatasetHyperlink> getHyperlinks() {
    return this.hyperlinks;
  }

  @JsonProperty("hyperlinks")
  public void setHyperlinks(List<DatasetHyperlink> hyperlinks) {
    this.hyperlinks = hyperlinks;
  }

  @JsonProperty("taxonIds")
  public List<Long> getTaxonIds() {
    return this.taxonIds;
  }

  @JsonProperty("taxonIds")
  public void setTaxonIds(List<Long> taxonIds) {
    this.taxonIds = taxonIds;
  }

  @JsonProperty("contacts")
  public List<DatasetContact> getContacts() {
    return this.contacts;
  }

  @JsonProperty("contacts")
  public void setContacts(List<DatasetContact> contacts) {
    this.contacts = contacts;
  }
}
