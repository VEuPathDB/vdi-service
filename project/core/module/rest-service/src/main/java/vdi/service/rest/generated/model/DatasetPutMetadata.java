package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetPutMetadataImpl.class
)
public interface DatasetPutMetadata extends DatasetPatchRequestBody {
  @JsonProperty(JsonField.TYPE)
  DatasetTypePatch getType();

  @JsonProperty(JsonField.TYPE)
  void setType(DatasetTypePatch type);

  @JsonProperty(JsonField.VISIBILITY)
  VisibilityPatch getVisibility();

  @JsonProperty(JsonField.VISIBILITY)
  void setVisibility(VisibilityPatch visibility);

  @JsonProperty(JsonField.NAME)
  StringPatch getName();

  @JsonProperty(JsonField.NAME)
  void setName(StringPatch name);

  @JsonProperty(JsonField.SUMMARY)
  StringPatch getSummary();

  @JsonProperty(JsonField.SUMMARY)
  void setSummary(StringPatch summary);

  @JsonProperty(JsonField.DESCRIPTION)
  OptionalStringPatch getDescription();

  @JsonProperty(JsonField.DESCRIPTION)
  void setDescription(OptionalStringPatch description);

  @JsonProperty(JsonField.PUBLICATIONS)
  PublicationsPatch getPublications();

  @JsonProperty(JsonField.PUBLICATIONS)
  void setPublications(PublicationsPatch publications);

  @JsonProperty(JsonField.CONTACTS)
  ContactsPatch getContacts();

  @JsonProperty(JsonField.CONTACTS)
  void setContacts(ContactsPatch contacts);

  @JsonProperty(JsonField.PROJECT_NAME)
  OptionalStringPatch getProjectName();

  @JsonProperty(JsonField.PROJECT_NAME)
  void setProjectName(OptionalStringPatch projectName);

  @JsonProperty(JsonField.PROGRAM_NAME)
  OptionalStringPatch getProgramName();

  @JsonProperty(JsonField.PROGRAM_NAME)
  void setProgramName(OptionalStringPatch programName);

  @JsonProperty(JsonField.LINKED_DATASETS)
  LinkedDatasetPatch getLinkedDatasets();

  @JsonProperty(JsonField.LINKED_DATASETS)
  void setLinkedDatasets(LinkedDatasetPatch linkedDatasets);

  @JsonProperty(JsonField.EXPERIMENTAL_ORGANISM)
  OrganismPatch getExperimentalOrganism();

  @JsonProperty(JsonField.EXPERIMENTAL_ORGANISM)
  void setExperimentalOrganism(OrganismPatch experimentalOrganism);

  @JsonProperty(JsonField.HOST_ORGANISM)
  OrganismPatch getHostOrganism();

  @JsonProperty(JsonField.HOST_ORGANISM)
  void setHostOrganism(OrganismPatch hostOrganism);

  @JsonProperty(JsonField.STUDY_CHARACTERISTICS)
  DatasetCharacteristicsPatch getStudyCharacteristics();

  @JsonProperty(JsonField.STUDY_CHARACTERISTICS)
  void setStudyCharacteristics(DatasetCharacteristicsPatch studyCharacteristics);

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  ExternalIdentifiersPatch getExternalIdentifiers();

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  void setExternalIdentifiers(ExternalIdentifiersPatch externalIdentifiers);

  @JsonProperty(JsonField.FUNDING)
  FundingPatch getFunding();

  @JsonProperty(JsonField.FUNDING)
  void setFunding(FundingPatch funding);

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  OptionalStringPatch getShortAttribution();

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  void setShortAttribution(OptionalStringPatch shortAttribution);

  @JsonProperty(JsonField.ORIGIN)
  String getOrigin();

  @JsonProperty(JsonField.ORIGIN)
  void setOrigin(String origin);

  @JsonProperty(JsonField.REVISION_NOTE)
  String getRevisionNote();

  @JsonProperty(JsonField.REVISION_NOTE)
  void setRevisionNote(String revisionNote);
}
