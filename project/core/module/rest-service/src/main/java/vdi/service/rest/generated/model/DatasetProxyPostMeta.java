package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;
import java.util.List;

@JsonDeserialize(
    as = DatasetProxyPostMetaImpl.class
)
public interface DatasetProxyPostMeta extends DatasetPostMeta {
  @JsonProperty(JsonField.INSTALL_TARGETS)
  List<String> getInstallTargets();

  @JsonProperty(JsonField.INSTALL_TARGETS)
  void setInstallTargets(List<String> installTargets);

  @JsonProperty(JsonField.NAME)
  String getName();

  @JsonProperty(JsonField.NAME)
  void setName(String name);

  @JsonProperty(JsonField.SUMMARY)
  String getSummary();

  @JsonProperty(JsonField.SUMMARY)
  void setSummary(String summary);

  @JsonProperty(JsonField.DESCRIPTION)
  String getDescription();

  @JsonProperty(JsonField.DESCRIPTION)
  void setDescription(String description);

  @JsonProperty(JsonField.ORIGIN)
  String getOrigin();

  @JsonProperty(JsonField.ORIGIN)
  void setOrigin(String origin);

  @JsonProperty(JsonField.DEPENDENCIES)
  List<DatasetDependency> getDependencies();

  @JsonProperty(JsonField.DEPENDENCIES)
  void setDependencies(List<DatasetDependency> dependencies);

  @JsonProperty(JsonField.PUBLICATIONS)
  List<DatasetPublication> getPublications();

  @JsonProperty(JsonField.PUBLICATIONS)
  void setPublications(List<DatasetPublication> publications);

  @JsonProperty(JsonField.CONTACTS)
  List<DatasetContact> getContacts();

  @JsonProperty(JsonField.CONTACTS)
  void setContacts(List<DatasetContact> contacts);

  @JsonProperty(JsonField.PROJECT_NAME)
  String getProjectName();

  @JsonProperty(JsonField.PROJECT_NAME)
  void setProjectName(String projectName);

  @JsonProperty(JsonField.PROGRAM_NAME)
  String getProgramName();

  @JsonProperty(JsonField.PROGRAM_NAME)
  void setProgramName(String programName);

  @JsonProperty(JsonField.LINKED_DATASETS)
  List<LinkedDataset> getLinkedDatasets();

  @JsonProperty(JsonField.LINKED_DATASETS)
  void setLinkedDatasets(List<LinkedDataset> linkedDatasets);

  @JsonProperty(JsonField.EXPERIMENTAL_ORGANISM)
  DatasetOrganism getExperimentalOrganism();

  @JsonProperty(JsonField.EXPERIMENTAL_ORGANISM)
  void setExperimentalOrganism(DatasetOrganism experimentalOrganism);

  @JsonProperty(JsonField.HOST_ORGANISM)
  DatasetOrganism getHostOrganism();

  @JsonProperty(JsonField.HOST_ORGANISM)
  void setHostOrganism(DatasetOrganism hostOrganism);

  @JsonProperty(JsonField.STUDY_CHARACTERISTICS)
  DatasetCharacteristics getStudyCharacteristics();

  @JsonProperty(JsonField.STUDY_CHARACTERISTICS)
  void setStudyCharacteristics(DatasetCharacteristics studyCharacteristics);

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  ExternalIdentifiers getExternalIdentifiers();

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  void setExternalIdentifiers(ExternalIdentifiers externalIdentifiers);

  @JsonProperty(JsonField.FUNDING)
  List<DatasetFundingAward> getFunding();

  @JsonProperty(JsonField.FUNDING)
  void setFunding(List<DatasetFundingAward> funding);

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  String getShortAttribution();

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  void setShortAttribution(String shortAttribution);

  @JsonProperty(JsonField.TYPE)
  DatasetTypeInput getType();

  @JsonProperty(JsonField.TYPE)
  void setType(DatasetTypeInput type);

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

  @JsonProperty(JsonField.CREATED_ON)
  OffsetDateTime getCreatedOn();

  @JsonProperty(JsonField.CREATED_ON)
  void setCreatedOn(OffsetDateTime createdOn);
}
