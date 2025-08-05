package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;
import java.util.List;

@JsonDeserialize(
    as = DatasetDetailsImpl.class
)
public interface DatasetDetails extends DatasetMetaBase {
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

  @JsonProperty(JsonField.RELATED_STUDIES)
  List<RelatedStudy> getRelatedStudies();

  @JsonProperty(JsonField.RELATED_STUDIES)
  void setRelatedStudies(List<RelatedStudy> relatedStudies);

  @JsonProperty(JsonField.EXPERIMENTAL_ORGANISM)
  DatasetOrganism getExperimentalOrganism();

  @JsonProperty(JsonField.EXPERIMENTAL_ORGANISM)
  void setExperimentalOrganism(DatasetOrganism experimentalOrganism);

  @JsonProperty(JsonField.HOST_ORGANISM)
  DatasetOrganism getHostOrganism();

  @JsonProperty(JsonField.HOST_ORGANISM)
  void setHostOrganism(DatasetOrganism hostOrganism);

  @JsonProperty(JsonField.STUDY_CHARACTERISTICS)
  StudyCharacteristics getStudyCharacteristics();

  @JsonProperty(JsonField.STUDY_CHARACTERISTICS)
  void setStudyCharacteristics(StudyCharacteristics studyCharacteristics);

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  ExternalIdentifiers getExternalIdentifiers();

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  void setExternalIdentifiers(ExternalIdentifiers externalIdentifiers);

  @JsonProperty(JsonField.FUNDING)
  List<DatasetFundingAward> getFunding();

  @JsonProperty(JsonField.FUNDING)
  void setFunding(List<DatasetFundingAward> funding);

  @JsonProperty(JsonField.DATASET_ID)
  String getDatasetId();

  @JsonProperty(JsonField.DATASET_ID)
  void setDatasetId(String datasetId);

  @JsonProperty(JsonField.TYPE)
  DatasetTypeOutput getType();

  @JsonProperty(JsonField.TYPE)
  void setType(DatasetTypeOutput type);

  @JsonProperty(JsonField.VISIBILITY)
  DatasetVisibility getVisibility();

  @JsonProperty(JsonField.VISIBILITY)
  void setVisibility(DatasetVisibility visibility);

  @JsonProperty(JsonField.OWNER)
  DatasetOwner getOwner();

  @JsonProperty(JsonField.OWNER)
  void setOwner(DatasetOwner owner);

  @JsonProperty(JsonField.CREATED)
  Date getCreated();

  @JsonProperty(JsonField.CREATED)
  void setCreated(Date created);

  @JsonProperty(JsonField.SOURCE_URL)
  String getSourceUrl();

  @JsonProperty(JsonField.SOURCE_URL)
  void setSourceUrl(String sourceUrl);

  @JsonProperty(JsonField.REVISION_HISTORY)
  RevisionHistory getRevisionHistory();

  @JsonProperty(JsonField.REVISION_HISTORY)
  void setRevisionHistory(RevisionHistory revisionHistory);

  @JsonProperty(JsonField.IMPORT_MESSAGES)
  List<String> getImportMessages();

  @JsonProperty(JsonField.IMPORT_MESSAGES)
  void setImportMessages(List<String> importMessages);

  @JsonProperty(JsonField.SHARES)
  List<ShareOffer> getShares();

  @JsonProperty(JsonField.SHARES)
  void setShares(List<ShareOffer> shares);

  @JsonProperty(JsonField.STATUS)
  DatasetStatusInfo getStatus();

  @JsonProperty(JsonField.STATUS)
  void setStatus(DatasetStatusInfo status);
}
