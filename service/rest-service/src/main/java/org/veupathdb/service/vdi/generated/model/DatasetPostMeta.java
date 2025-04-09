package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;
import java.util.List;

@JsonDeserialize(
    as = DatasetPostMetaImpl.class
)
public interface DatasetPostMeta {
  @JsonProperty(JsonField.NAME)
  String getName();

  @JsonProperty(JsonField.NAME)
  void setName(String name);

  @JsonProperty(JsonField.DATASET_TYPE)
  DatasetTypeRequestBody getDatasetType();

  @JsonProperty(JsonField.DATASET_TYPE)
  void setDatasetType(DatasetTypeRequestBody datasetType);

  @JsonProperty(JsonField.ORIGIN)
  String getOrigin();

  @JsonProperty(JsonField.ORIGIN)
  void setOrigin(String origin);

  @JsonProperty(JsonField.DEPENDENCIES)
  List<DatasetDependency> getDependencies();

  @JsonProperty(JsonField.DEPENDENCIES)
  void setDependencies(List<DatasetDependency> dependencies);

  @JsonProperty(JsonField.PROJECTS)
  List<String> getProjects();

  @JsonProperty(JsonField.PROJECTS)
  void setProjects(List<String> projects);

  @JsonProperty(JsonField.SHORT_NAME)
  String getShortName();

  @JsonProperty(JsonField.SHORT_NAME)
  void setShortName(String shortName);

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  String getShortAttribution();

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  void setShortAttribution(String shortAttribution);

  @JsonProperty(JsonField.CATEGORY)
  String getCategory();

  @JsonProperty(JsonField.CATEGORY)
  void setCategory(String category);

  @JsonProperty(
      value = JsonField.VISIBILITY,
      defaultValue = "private"
  )
  DatasetVisibility getVisibility();

  @JsonProperty(
      value = JsonField.VISIBILITY,
      defaultValue = "private"
  )
  void setVisibility(DatasetVisibility visibility);

  @JsonProperty(JsonField.SUMMARY)
  String getSummary();

  @JsonProperty(JsonField.SUMMARY)
  void setSummary(String summary);

  @JsonProperty(JsonField.DESCRIPTION)
  String getDescription();

  @JsonProperty(JsonField.DESCRIPTION)
  void setDescription(String description);

  @JsonProperty(JsonField.PUBLICATIONS)
  List<DatasetPublication> getPublications();

  @JsonProperty(JsonField.PUBLICATIONS)
  void setPublications(List<DatasetPublication> publications);

  @JsonProperty(JsonField.HYPERLINKS)
  List<DatasetHyperlink> getHyperlinks();

  @JsonProperty(JsonField.HYPERLINKS)
  void setHyperlinks(List<DatasetHyperlink> hyperlinks);

  @JsonProperty(JsonField.ORGANISMS)
  List<String> getOrganisms();

  @JsonProperty(JsonField.ORGANISMS)
  void setOrganisms(List<String> organisms);

  @JsonProperty(JsonField.CONTACTS)
  List<DatasetContact> getContacts();

  @JsonProperty(JsonField.CONTACTS)
  void setContacts(List<DatasetContact> contacts);

  @JsonProperty(JsonField.CREATED_ON)
  OffsetDateTime getCreatedOn();

  @JsonProperty(JsonField.CREATED_ON)
  void setCreatedOn(OffsetDateTime createdOn);
}
