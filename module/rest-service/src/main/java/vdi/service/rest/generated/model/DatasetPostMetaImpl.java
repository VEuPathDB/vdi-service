package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
    "datasetType",
    "projects",
    "visibility"
})
public class DatasetPostMetaImpl implements DatasetPostMeta {
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

  @JsonProperty(JsonField.DATASET_TYPE)
  private DatasetTypeInput datasetType;

  @JsonProperty(JsonField.PROJECTS)
  private List<String> projects;

  @JsonProperty(
      value = JsonField.VISIBILITY,
      defaultValue = "private"
  )
  private DatasetVisibility visibility;

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

  @JsonProperty(JsonField.DATASET_TYPE)
  public DatasetTypeInput getDatasetType() {
    return this.datasetType;
  }

  @JsonProperty(JsonField.DATASET_TYPE)
  public void setDatasetType(DatasetTypeInput datasetType) {
    this.datasetType = datasetType;
  }

  @JsonProperty(JsonField.PROJECTS)
  public List<String> getProjects() {
    return this.projects;
  }

  @JsonProperty(JsonField.PROJECTS)
  public void setProjects(List<String> projects) {
    this.projects = projects;
  }

  @JsonProperty(
      value = JsonField.VISIBILITY,
      defaultValue = "private"
  )
  public DatasetVisibility getVisibility() {
    return this.visibility;
  }

  @JsonProperty(
      value = JsonField.VISIBILITY,
      defaultValue = "private"
  )
  public void setVisibility(DatasetVisibility visibility) {
    this.visibility = visibility;
  }
}
