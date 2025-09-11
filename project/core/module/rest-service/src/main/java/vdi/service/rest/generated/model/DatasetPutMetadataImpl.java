package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "type",
    "visibility",
    "name",
    "summary",
    "description",
    "publications",
    "contacts",
    "projectName",
    "programName",
    "linkedDatasets",
    "experimentalOrganism",
    "hostOrganism",
    "characteristics",
    "externalIdentifiers",
    "funding",
    "shortAttribution",
    "origin",
    "revisionNote"
})
public class DatasetPutMetadataImpl implements DatasetPutMetadata {
  @JsonProperty(JsonField.TYPE)
  private DatasetTypePatch type;

  @JsonProperty(JsonField.VISIBILITY)
  private VisibilityPatch visibility;

  @JsonProperty(JsonField.NAME)
  private StringPatch name;

  @JsonProperty(JsonField.SUMMARY)
  private StringPatch summary;

  @JsonProperty(JsonField.DESCRIPTION)
  private StringPatch description;

  @JsonProperty(JsonField.PUBLICATIONS)
  private PublicationsPatch publications;

  @JsonProperty(JsonField.CONTACTS)
  private ContactsPatch contacts;

  @JsonProperty(JsonField.PROJECT_NAME)
  private StringPatch projectName;

  @JsonProperty(JsonField.PROGRAM_NAME)
  private StringPatch programName;

  @JsonProperty(JsonField.LINKED_DATASETS)
  private LinkedDatasetPatch linkedDatasets;

  @JsonProperty(JsonField.EXPERIMENTAL_ORGANISM)
  private OrganismPatch experimentalOrganism;

  @JsonProperty(JsonField.HOST_ORGANISM)
  private OrganismPatch hostOrganism;

  @JsonProperty(JsonField.CHARACTERISTICS)
  private DatasetCharacteristicsPatch characteristics;

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  private ExternalIdentifiersPatch externalIdentifiers;

  @JsonProperty(JsonField.FUNDING)
  private FundingPatch funding;

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  private StringPatch shortAttribution;

  @JsonProperty(JsonField.ORIGIN)
  private String origin;

  @JsonProperty(JsonField.REVISION_NOTE)
  private String revisionNote;

  @JsonProperty(JsonField.TYPE)
  public DatasetTypePatch getType() {
    return this.type;
  }

  @JsonProperty(JsonField.TYPE)
  public void setType(DatasetTypePatch type) {
    this.type = type;
  }

  @JsonProperty(JsonField.VISIBILITY)
  public VisibilityPatch getVisibility() {
    return this.visibility;
  }

  @JsonProperty(JsonField.VISIBILITY)
  public void setVisibility(VisibilityPatch visibility) {
    this.visibility = visibility;
  }

  @JsonProperty(JsonField.NAME)
  public StringPatch getName() {
    return this.name;
  }

  @JsonProperty(JsonField.NAME)
  public void setName(StringPatch name) {
    this.name = name;
  }

  @JsonProperty(JsonField.SUMMARY)
  public StringPatch getSummary() {
    return this.summary;
  }

  @JsonProperty(JsonField.SUMMARY)
  public void setSummary(StringPatch summary) {
    this.summary = summary;
  }

  @JsonProperty(JsonField.DESCRIPTION)
  public StringPatch getDescription() {
    return this.description;
  }

  @JsonProperty(JsonField.DESCRIPTION)
  public void setDescription(StringPatch description) {
    this.description = description;
  }

  @JsonProperty(JsonField.PUBLICATIONS)
  public PublicationsPatch getPublications() {
    return this.publications;
  }

  @JsonProperty(JsonField.PUBLICATIONS)
  public void setPublications(PublicationsPatch publications) {
    this.publications = publications;
  }

  @JsonProperty(JsonField.CONTACTS)
  public ContactsPatch getContacts() {
    return this.contacts;
  }

  @JsonProperty(JsonField.CONTACTS)
  public void setContacts(ContactsPatch contacts) {
    this.contacts = contacts;
  }

  @JsonProperty(JsonField.PROJECT_NAME)
  public StringPatch getProjectName() {
    return this.projectName;
  }

  @JsonProperty(JsonField.PROJECT_NAME)
  public void setProjectName(StringPatch projectName) {
    this.projectName = projectName;
  }

  @JsonProperty(JsonField.PROGRAM_NAME)
  public StringPatch getProgramName() {
    return this.programName;
  }

  @JsonProperty(JsonField.PROGRAM_NAME)
  public void setProgramName(StringPatch programName) {
    this.programName = programName;
  }

  @JsonProperty(JsonField.LINKED_DATASETS)
  public LinkedDatasetPatch getLinkedDatasets() {
    return this.linkedDatasets;
  }

  @JsonProperty(JsonField.LINKED_DATASETS)
  public void setLinkedDatasets(LinkedDatasetPatch linkedDatasets) {
    this.linkedDatasets = linkedDatasets;
  }

  @JsonProperty(JsonField.EXPERIMENTAL_ORGANISM)
  public OrganismPatch getExperimentalOrganism() {
    return this.experimentalOrganism;
  }

  @JsonProperty(JsonField.EXPERIMENTAL_ORGANISM)
  public void setExperimentalOrganism(OrganismPatch experimentalOrganism) {
    this.experimentalOrganism = experimentalOrganism;
  }

  @JsonProperty(JsonField.HOST_ORGANISM)
  public OrganismPatch getHostOrganism() {
    return this.hostOrganism;
  }

  @JsonProperty(JsonField.HOST_ORGANISM)
  public void setHostOrganism(OrganismPatch hostOrganism) {
    this.hostOrganism = hostOrganism;
  }

  @JsonProperty(JsonField.CHARACTERISTICS)
  public DatasetCharacteristicsPatch getCharacteristics() {
    return this.characteristics;
  }

  @JsonProperty(JsonField.CHARACTERISTICS)
  public void setCharacteristics(DatasetCharacteristicsPatch characteristics) {
    this.characteristics = characteristics;
  }

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  public ExternalIdentifiersPatch getExternalIdentifiers() {
    return this.externalIdentifiers;
  }

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  public void setExternalIdentifiers(ExternalIdentifiersPatch externalIdentifiers) {
    this.externalIdentifiers = externalIdentifiers;
  }

  @JsonProperty(JsonField.FUNDING)
  public FundingPatch getFunding() {
    return this.funding;
  }

  @JsonProperty(JsonField.FUNDING)
  public void setFunding(FundingPatch funding) {
    this.funding = funding;
  }

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  public StringPatch getShortAttribution() {
    return this.shortAttribution;
  }

  @JsonProperty(JsonField.SHORT_ATTRIBUTION)
  public void setShortAttribution(StringPatch shortAttribution) {
    this.shortAttribution = shortAttribution;
  }

  @JsonProperty(JsonField.ORIGIN)
  public String getOrigin() {
    return this.origin;
  }

  @JsonProperty(JsonField.ORIGIN)
  public void setOrigin(String origin) {
    this.origin = origin;
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
