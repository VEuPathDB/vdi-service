package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;
import java.util.List;

@JsonDeserialize(
    as = DatasetPostMetaImpl.class
)
public interface DatasetPostMeta {
  @JsonProperty("datasetType")
  DatasetPostType getDatasetType();

  @JsonProperty("datasetType")
  void setDatasetType(DatasetPostType datasetType);

  @JsonProperty("name")
  String getName();

  @JsonProperty("name")
  void setName(String name);

  @JsonProperty("shortName")
  String getShortName();

  @JsonProperty("shortName")
  void setShortName(String shortName);

  @JsonProperty("shortAttribution")
  String getShortAttribution();

  @JsonProperty("shortAttribution")
  void setShortAttribution(String shortAttribution);

  @JsonProperty("category")
  String getCategory();

  @JsonProperty("category")
  void setCategory(String category);

  @JsonProperty(
      value = "visibility",
      defaultValue = "private"
  )
  DatasetVisibility getVisibility();

  @JsonProperty(
      value = "visibility",
      defaultValue = "private"
  )
  void setVisibility(DatasetVisibility visibility);

  @JsonProperty("summary")
  String getSummary();

  @JsonProperty("summary")
  void setSummary(String summary);

  @JsonProperty("description")
  String getDescription();

  @JsonProperty("description")
  void setDescription(String description);

  @JsonProperty("origin")
  String getOrigin();

  @JsonProperty("origin")
  void setOrigin(String origin);

  @JsonProperty("projects")
  List<String> getProjects();

  @JsonProperty("projects")
  void setProjects(List<String> projects);

  @JsonProperty("dependencies")
  List<DatasetDependency> getDependencies();

  @JsonProperty("dependencies")
  void setDependencies(List<DatasetDependency> dependencies);

  @JsonProperty("publications")
  List<DatasetPublication> getPublications();

  @JsonProperty("publications")
  void setPublications(List<DatasetPublication> publications);

  @JsonProperty("hyperlinks")
  List<DatasetHyperlink> getHyperlinks();

  @JsonProperty("hyperlinks")
  void setHyperlinks(List<DatasetHyperlink> hyperlinks);

  @JsonProperty("taxonIds")
  List<Long> getTaxonIds();

  @JsonProperty("taxonIds")
  void setTaxonIds(List<Long> taxonIds);

  @JsonProperty("contacts")
  List<DatasetContact> getContacts();

  @JsonProperty("contacts")
  void setContacts(List<DatasetContact> contacts);

  @JsonProperty("createdOn")
  OffsetDateTime getCreatedOn();

  @JsonProperty("createdOn")
  void setCreatedOn(OffsetDateTime createdOn);
}
