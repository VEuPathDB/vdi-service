package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetType",
    "name",
    "shortName",
    "shortAttribution",
    "category",
    "visibility",
    "summary",
    "description",
    "origin",
    "projects",
    "dependencies",
    "publications",
    "hyperlinks",
    "taxonIds",
    "contacts",
    "createdOn"
})
public class DatasetPostMetaImpl implements DatasetPostMeta {
  @JsonProperty("datasetType")
  private DatasetPostType datasetType;

  @JsonProperty("name")
  private String name;

  @JsonProperty("shortName")
  private String shortName;

  @JsonProperty("shortAttribution")
  private String shortAttribution;

  @JsonProperty("category")
  private String category;

  @JsonProperty(
      value = "visibility",
      defaultValue = "private"
  )
  private DatasetVisibility visibility;

  @JsonProperty("summary")
  private String summary;

  @JsonProperty("description")
  private String description;

  @JsonProperty("origin")
  private String origin;

  @JsonProperty("projects")
  private List<String> projects;

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

  @JsonProperty("createdOn")

  private OffsetDateTime createdOn;

  @JsonProperty("datasetType")
  public DatasetPostType getDatasetType() {
    return this.datasetType;
  }

  @JsonProperty("datasetType")
  public void setDatasetType(DatasetPostType datasetType) {
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

  @JsonProperty(
      value = "visibility",
      defaultValue = "private"
  )
  public DatasetVisibility getVisibility() {
    return this.visibility;
  }

  @JsonProperty(
      value = "visibility",
      defaultValue = "private"
  )
  public void setVisibility(DatasetVisibility visibility) {
    this.visibility = visibility;
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

  @JsonProperty("origin")
  public String getOrigin() {
    return this.origin;
  }

  @JsonProperty("origin")
  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @JsonProperty("projects")
  public List<String> getProjects() {
    return this.projects;
  }

  @JsonProperty("projects")
  public void setProjects(List<String> projects) {
    this.projects = projects;
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

  @JsonProperty("createdOn")
  public OffsetDateTime getCreatedOn() {
    return this.createdOn;
  }

  @JsonProperty("createdOn")
  public void setCreatedOn(OffsetDateTime createdOn) {
    this.createdOn = createdOn;
  }
}
