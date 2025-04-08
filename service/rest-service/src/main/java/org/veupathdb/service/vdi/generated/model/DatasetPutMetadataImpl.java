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
    "contacts",
    "revisionNote"
})
public class DatasetPutMetadataImpl implements DatasetPutMetadata {
  @JsonProperty(JsonField.VISIBILITY)
  private DatasetVisibility visibility;

  @JsonProperty(JsonField.NAME)
  private String name;

  @JsonProperty(JsonField.SHORT_NAME)
  private String shortName;

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  private String shortAttribution;

  @JsonProperty(JsonField.CATEGORY)
  private String category;

  @JsonProperty(JsonField.SUMMARY)
  private String summary;

  @JsonProperty(JsonField.DESCRIPTION)
  private String description;

  @JsonProperty(JsonField.PUBLICATIONS)
  private List<DatasetPublication> publications;

  @JsonProperty(JsonField.HYPERLINKS)
  private List<DatasetHyperlink> hyperlinks;

  @JsonProperty(JsonField.ORGANISMS)
  private List<String> organisms;

  @JsonProperty(JsonField.CONTACTS)
  private List<DatasetContact> contacts;

  @JsonProperty(JsonField.REVISION_NOTE)
  private String revisionNote;

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

  @JsonProperty(JsonField.CATEGORY)
  public String getCategory() {
    return this.category;
  }

  @JsonProperty(JsonField.CATEGORY)
  public void setCategory(String category) {
    this.category = category;
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

  @JsonProperty(JsonField.PUBLICATIONS)
  public List<DatasetPublication> getPublications() {
    return this.publications;
  }

  @JsonProperty(JsonField.PUBLICATIONS)
  public void setPublications(List<DatasetPublication> publications) {
    this.publications = publications;
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

  @JsonProperty(JsonField.CONTACTS)
  public List<DatasetContact> getContacts() {
    return this.contacts;
  }

  @JsonProperty(JsonField.CONTACTS)
  public void setContacts(List<DatasetContact> contacts) {
    this.contacts = contacts;
  }

  @JsonProperty(JsonField.REVISION_NOTE)
  public String getRevisionNote() {
    return this.revisionNote;
  }

  @JsonProperty(JsonField.REVISION_NOTE)
  public void setRevisionNote(String revisionNote) {
    this.revisionNote = revisionNote;
  }
}
