package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetPatchRequestBodyImpl.class
)
public interface DatasetPatchRequestBody {
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
  StringPatch getDescription();

  @JsonProperty(JsonField.DESCRIPTION)
  void setDescription(StringPatch description);

  @JsonProperty(JsonField.PUBLICATIONS)
  PublicationsPatch getPublications();

  @JsonProperty(JsonField.PUBLICATIONS)
  void setPublications(PublicationsPatch publications);

  @JsonProperty(JsonField.CONTACTS)
  ContactsPatch getContacts();

  @JsonProperty(JsonField.CONTACTS)
  void setContacts(ContactsPatch contacts);

  @JsonProperty(JsonField.PROJECT_NAME)
  StringPatch getProjectName();

  @JsonProperty(JsonField.PROJECT_NAME)
  void setProjectName(StringPatch projectName);

  @JsonProperty(JsonField.PROGRAM_NAME)
  StringPatch getProgramName();

  @JsonProperty(JsonField.PROGRAM_NAME)
  void setProgramName(StringPatch programName);

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

  @JsonProperty(JsonField.CHARACTERISTICS)
  DatasetCharacteristicsPatch getCharacteristics();

  @JsonProperty(JsonField.CHARACTERISTICS)
  void setCharacteristics(DatasetCharacteristicsPatch characteristics);

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  ExternalIdentifiersPatch getExternalIdentifiers();

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  void setExternalIdentifiers(ExternalIdentifiersPatch externalIdentifiers);

  @JsonProperty(JsonField.FUNDING)
  FundingPatch getFunding();

  @JsonProperty(JsonField.FUNDING)
  void setFunding(FundingPatch funding);

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  StringPatch getShortAttribution();

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  void setShortAttribution(StringPatch shortAttribution);
}
