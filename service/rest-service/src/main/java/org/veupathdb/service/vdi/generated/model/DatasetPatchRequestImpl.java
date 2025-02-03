package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "visibility",
    "name",
    "shortName",
    "shortAttribution",
    "category",
    "summary",
    "description",
    "publications",
    "hyperlinks",
    "organisms",
    "contacts"
})
public class DatasetPatchRequestImpl implements DatasetPatchRequest {
  @JsonProperty("visibility")
  private DatasetVisibility visibility;

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

  @JsonProperty("publications")
  private List<DatasetPublication> publications;

  @JsonProperty("hyperlinks")
  private List<DatasetHyperlink> hyperlinks;

  @JsonProperty("organisms")
  private List<String> organisms;

  @JsonProperty("contacts")
  private List<DatasetContact> contacts;

  @JsonProperty("visibility")
  public DatasetVisibility getVisibility() {
    return this.visibility;
  }

  @JsonProperty("visibility")
  public void setVisibility(DatasetVisibility visibility) {
    this.visibility = visibility;
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

  @JsonProperty("organisms")
  public List<String> getOrganisms() {
    return this.organisms;
  }

  @JsonProperty("organisms")
  public void setOrganisms(List<String> organisms) {
    this.organisms = organisms;
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
