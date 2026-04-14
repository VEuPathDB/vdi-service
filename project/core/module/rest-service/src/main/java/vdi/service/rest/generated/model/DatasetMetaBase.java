package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetMetaBaseImpl.class
)
public interface DatasetMetaBase {
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

  @JsonProperty(JsonField.DATASET_CHARACTERISTICS)
  DatasetCharacteristics getDatasetCharacteristics();

  @JsonProperty(JsonField.DATASET_CHARACTERISTICS)
  void setDatasetCharacteristics(DatasetCharacteristics datasetCharacteristics);

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

  @JsonProperty(
      value = JsonField.DAYS_FOR_APPROVAL,
      defaultValue = "-1"
  )
  Integer getDaysForApproval();

  @JsonProperty(
      value = JsonField.DAYS_FOR_APPROVAL,
      defaultValue = "-1"
  )
  void setDaysForApproval(Integer daysForApproval);

  @JsonProperty(JsonField.DATA_DISCLAIMER)
  String getDataDisclaimer();

  @JsonProperty(JsonField.DATA_DISCLAIMER)
  void setDataDisclaimer(String dataDisclaimer);
}
