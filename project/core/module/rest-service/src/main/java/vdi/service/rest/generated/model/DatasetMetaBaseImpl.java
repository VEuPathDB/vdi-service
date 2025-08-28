package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "installTargets",
    "name",
    "summary",
    "description",
    "origin",
    "dependencies",
    "publications",
    "contacts",
    "projectName",
    "programName",
    "linkedDatasets",
    "experimentalOrganism",
    "hostOrganism",
    "studyCharacteristics",
    "externalIdentifiers",
    "funding"
})
public class DatasetMetaBaseImpl implements DatasetMetaBase {
  @JsonProperty(JsonField.INSTALL_TARGETS)
  private List<String> installTargets;

  @JsonProperty(JsonField.NAME)
  private String name;

  @JsonProperty(JsonField.SUMMARY)
  private String summary;

  @JsonProperty(JsonField.DESCRIPTION)
  private String description;

  @JsonProperty(JsonField.ORIGIN)
  private String origin;

  @JsonProperty(JsonField.DEPENDENCIES)
  private List<DatasetDependency> dependencies;

  @JsonProperty(JsonField.PUBLICATIONS)
  private List<DatasetPublication> publications;

  @JsonProperty(JsonField.CONTACTS)
  private List<DatasetContact> contacts;

  @JsonProperty(JsonField.PROJECT_NAME)
  private String projectName;

  @JsonProperty(JsonField.PROGRAM_NAME)
  private String programName;

  @JsonProperty(JsonField.LINKED_DATASETS)
  private List<LinkedDataset> linkedDatasets;

  @JsonProperty(JsonField.EXPERIMENTAL_ORGANISM)
  private DatasetOrganism experimentalOrganism;

  @JsonProperty(JsonField.HOST_ORGANISM)
  private DatasetOrganism hostOrganism;

  @JsonProperty(JsonField.STUDY_CHARACTERISTICS)
  private StudyCharacteristics studyCharacteristics;

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  private ExternalIdentifiers externalIdentifiers;

  @JsonProperty(JsonField.FUNDING)
  private List<DatasetFundingAward> funding;

  @JsonProperty(JsonField.INSTALL_TARGETS)
  public List<String> getInstallTargets() {
    return this.installTargets;
  }

  @JsonProperty(JsonField.INSTALL_TARGETS)
  public void setInstallTargets(List<String> installTargets) {
    this.installTargets = installTargets;
  }

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

  @JsonProperty(JsonField.DESCRIPTION)
  public String getDescription() {
    return this.description;
  }

  @JsonProperty(JsonField.DESCRIPTION)
  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty(JsonField.ORIGIN)
  public String getOrigin() {
    return this.origin;
  }

  @JsonProperty(JsonField.ORIGIN)
  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @JsonProperty(JsonField.DEPENDENCIES)
  public List<DatasetDependency> getDependencies() {
    return this.dependencies;
  }

  @JsonProperty(JsonField.DEPENDENCIES)
  public void setDependencies(List<DatasetDependency> dependencies) {
    this.dependencies = dependencies;
  }

  @JsonProperty(JsonField.PUBLICATIONS)
  public List<DatasetPublication> getPublications() {
    return this.publications;
  }

  @JsonProperty(JsonField.PUBLICATIONS)
  public void setPublications(List<DatasetPublication> publications) {
    this.publications = publications;
  }

  @JsonProperty(JsonField.CONTACTS)
  public List<DatasetContact> getContacts() {
    return this.contacts;
  }

  @JsonProperty(JsonField.CONTACTS)
  public void setContacts(List<DatasetContact> contacts) {
    this.contacts = contacts;
  }

  @JsonProperty(JsonField.PROJECT_NAME)
  public String getProjectName() {
    return this.projectName;
  }

  @JsonProperty(JsonField.PROJECT_NAME)
  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  @JsonProperty(JsonField.PROGRAM_NAME)
  public String getProgramName() {
    return this.programName;
  }

  @JsonProperty(JsonField.PROGRAM_NAME)
  public void setProgramName(String programName) {
    this.programName = programName;
  }

  @JsonProperty(JsonField.LINKED_DATASETS)
  public List<LinkedDataset> getLinkedDatasets() {
    return this.linkedDatasets;
  }

  @JsonProperty(JsonField.LINKED_DATASETS)
  public void setLinkedDatasets(List<LinkedDataset> linkedDatasets) {
    this.linkedDatasets = linkedDatasets;
  }

  @JsonProperty(JsonField.EXPERIMENTAL_ORGANISM)
  public DatasetOrganism getExperimentalOrganism() {
    return this.experimentalOrganism;
  }

  @JsonProperty(JsonField.EXPERIMENTAL_ORGANISM)
  public void setExperimentalOrganism(DatasetOrganism experimentalOrganism) {
    this.experimentalOrganism = experimentalOrganism;
  }

  @JsonProperty(JsonField.HOST_ORGANISM)
  public DatasetOrganism getHostOrganism() {
    return this.hostOrganism;
  }

  @JsonProperty(JsonField.HOST_ORGANISM)
  public void setHostOrganism(DatasetOrganism hostOrganism) {
    this.hostOrganism = hostOrganism;
  }

  @JsonProperty(JsonField.STUDY_CHARACTERISTICS)
  public StudyCharacteristics getStudyCharacteristics() {
    return this.studyCharacteristics;
  }

  @JsonProperty(JsonField.STUDY_CHARACTERISTICS)
  public void setStudyCharacteristics(StudyCharacteristics studyCharacteristics) {
    this.studyCharacteristics = studyCharacteristics;
  }

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  public ExternalIdentifiers getExternalIdentifiers() {
    return this.externalIdentifiers;
  }

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  public void setExternalIdentifiers(ExternalIdentifiers externalIdentifiers) {
    this.externalIdentifiers = externalIdentifiers;
  }

  @JsonProperty(JsonField.FUNDING)
  public List<DatasetFundingAward> getFunding() {
    return this.funding;
  }

  @JsonProperty(JsonField.FUNDING)
  public void setFunding(List<DatasetFundingAward> funding) {
    this.funding = funding;
  }
}
