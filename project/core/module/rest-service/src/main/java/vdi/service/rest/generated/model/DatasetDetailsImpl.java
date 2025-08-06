package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;
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
    "relatedStudies",
    "experimentalOrganism",
    "hostOrganism",
    "studyCharacteristics",
    "externalIdentifiers",
    "funding",
    "datasetId",
    "type",
    "visibility",
    "owner",
    "created",
    "sourceUrl",
    "revisionHistory",
    "importMessages",
    "shares",
    "status"
})
public class DatasetDetailsImpl implements DatasetDetails {
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

  @JsonProperty(JsonField.RELATED_STUDIES)
  private List<RelatedStudy> relatedStudies;

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

  @JsonProperty(JsonField.DATASET_ID)
  private String datasetId;

  @JsonProperty(JsonField.TYPE)
  private DatasetTypeOutput type;

  @JsonProperty(JsonField.VISIBILITY)
  private DatasetVisibility visibility;

  @JsonProperty(JsonField.OWNER)
  private DatasetOwner owner;

  @JsonProperty(JsonField.CREATED)
  private OffsetDateTime created;

  @JsonProperty(JsonField.SOURCE_URL)
  private String sourceUrl;

  @JsonProperty(JsonField.REVISION_HISTORY)
  private RevisionHistory revisionHistory;

  @JsonProperty(JsonField.IMPORT_MESSAGES)
  private List<String> importMessages;

  @JsonProperty(JsonField.SHARES)
  private List<ShareOffer> shares;

  @JsonProperty(JsonField.STATUS)
  private DatasetStatusInfo status;

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

  @JsonProperty(JsonField.RELATED_STUDIES)
  public List<RelatedStudy> getRelatedStudies() {
    return this.relatedStudies;
  }

  @JsonProperty(JsonField.RELATED_STUDIES)
  public void setRelatedStudies(List<RelatedStudy> relatedStudies) {
    this.relatedStudies = relatedStudies;
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

  @JsonProperty(JsonField.DATASET_ID)
  public String getDatasetId() {
    return this.datasetId;
  }

  @JsonProperty(JsonField.DATASET_ID)
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  @JsonProperty(JsonField.TYPE)
  public DatasetTypeOutput getType() {
    return this.type;
  }

  @JsonProperty(JsonField.TYPE)
  public void setType(DatasetTypeOutput type) {
    this.type = type;
  }

  @JsonProperty(JsonField.VISIBILITY)
  public DatasetVisibility getVisibility() {
    return this.visibility;
  }

  @JsonProperty(JsonField.VISIBILITY)
  public void setVisibility(DatasetVisibility visibility) {
    this.visibility = visibility;
  }

  @JsonProperty(JsonField.OWNER)
  public DatasetOwner getOwner() {
    return this.owner;
  }

  @JsonProperty(JsonField.OWNER)
  public void setOwner(DatasetOwner owner) {
    this.owner = owner;
  }

  @JsonProperty(JsonField.CREATED)
  public OffsetDateTime getCreated() {
    return this.created;
  }

  @JsonProperty(JsonField.CREATED)
  public void setCreated(OffsetDateTime created) {
    this.created = created;
  }

  @JsonProperty(JsonField.SOURCE_URL)
  public String getSourceUrl() {
    return this.sourceUrl;
  }

  @JsonProperty(JsonField.SOURCE_URL)
  public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  @JsonProperty(JsonField.REVISION_HISTORY)
  public RevisionHistory getRevisionHistory() {
    return this.revisionHistory;
  }

  @JsonProperty(JsonField.REVISION_HISTORY)
  public void setRevisionHistory(RevisionHistory revisionHistory) {
    this.revisionHistory = revisionHistory;
  }

  @JsonProperty(JsonField.IMPORT_MESSAGES)
  public List<String> getImportMessages() {
    return this.importMessages;
  }

  @JsonProperty(JsonField.IMPORT_MESSAGES)
  public void setImportMessages(List<String> importMessages) {
    this.importMessages = importMessages;
  }

  @JsonProperty(JsonField.SHARES)
  public List<ShareOffer> getShares() {
    return this.shares;
  }

  @JsonProperty(JsonField.SHARES)
  public void setShares(List<ShareOffer> shares) {
    this.shares = shares;
  }

  @JsonProperty(JsonField.STATUS)
  public DatasetStatusInfo getStatus() {
    return this.status;
  }

  @JsonProperty(JsonField.STATUS)
  public void setStatus(DatasetStatusInfo status) {
    this.status = status;
  }
}
