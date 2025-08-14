package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

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
    "relatedStudies",
    "studyCharacteristics",
    "externalIdentifiers",
    "funding",
    "origin",
    "revisionNote"
})
public class DatasetPutMetadataImpl implements DatasetPutMetadata {
  @JsonProperty(JsonField.TYPE)
  private DatasetPutMetadata.TypeType type;

  @JsonProperty(JsonField.VISIBILITY)
  private DatasetPutMetadata.VisibilityType visibility;

  @JsonProperty(JsonField.NAME)
  private DatasetPutMetadata.NameType name;

  @JsonProperty(JsonField.SUMMARY)
  private DatasetPutMetadata.SummaryType summary;

  @JsonProperty(JsonField.DESCRIPTION)
  private DatasetPutMetadata.DescriptionType description;

  @JsonProperty(JsonField.PUBLICATIONS)
  private DatasetPutMetadata.PublicationsType publications;

  @JsonProperty(JsonField.CONTACTS)
  private DatasetPutMetadata.ContactsType contacts;

  @JsonProperty(JsonField.PROJECT_NAME)
  private DatasetPutMetadata.ProjectNameType projectName;

  @JsonProperty(JsonField.PROGRAM_NAME)
  private DatasetPutMetadata.ProgramNameType programName;

  @JsonProperty(JsonField.RELATED_STUDIES)
  private DatasetPutMetadata.RelatedStudiesType relatedStudies;

  @JsonProperty(JsonField.STUDY_CHARACTERISTICS)
  private StudyCharacteristicsPatch studyCharacteristics;

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  private ExternalIdentifiersPatch externalIdentifiers;

  @JsonProperty(JsonField.FUNDING)
  private DatasetPutMetadata.FundingType funding;

  @JsonProperty(JsonField.ORIGIN)
  private String origin;

  @JsonProperty(JsonField.REVISION_NOTE)
  private String revisionNote;

  @JsonProperty(JsonField.TYPE)
  public DatasetPutMetadata.TypeType getType() {
    return this.type;
  }

  @JsonProperty(JsonField.TYPE)
  public void setType(DatasetPutMetadata.TypeType type) {
    this.type = type;
  }

  @JsonProperty(JsonField.VISIBILITY)
  public DatasetPutMetadata.VisibilityType getVisibility() {
    return this.visibility;
  }

  @JsonProperty(JsonField.VISIBILITY)
  public void setVisibility(DatasetPutMetadata.VisibilityType visibility) {
    this.visibility = visibility;
  }

  @JsonProperty(JsonField.NAME)
  public DatasetPutMetadata.NameType getName() {
    return this.name;
  }

  @JsonProperty(JsonField.NAME)
  public void setName(DatasetPutMetadata.NameType name) {
    this.name = name;
  }

  @JsonProperty(JsonField.SUMMARY)
  public DatasetPutMetadata.SummaryType getSummary() {
    return this.summary;
  }

  @JsonProperty(JsonField.SUMMARY)
  public void setSummary(DatasetPutMetadata.SummaryType summary) {
    this.summary = summary;
  }

  @JsonProperty(JsonField.DESCRIPTION)
  public DatasetPutMetadata.DescriptionType getDescription() {
    return this.description;
  }

  @JsonProperty(JsonField.DESCRIPTION)
  public void setDescription(DatasetPutMetadata.DescriptionType description) {
    this.description = description;
  }

  @JsonProperty(JsonField.PUBLICATIONS)
  public DatasetPutMetadata.PublicationsType getPublications() {
    return this.publications;
  }

  @JsonProperty(JsonField.PUBLICATIONS)
  public void setPublications(DatasetPutMetadata.PublicationsType publications) {
    this.publications = publications;
  }

  @JsonProperty(JsonField.CONTACTS)
  public DatasetPutMetadata.ContactsType getContacts() {
    return this.contacts;
  }

  @JsonProperty(JsonField.CONTACTS)
  public void setContacts(DatasetPutMetadata.ContactsType contacts) {
    this.contacts = contacts;
  }

  @JsonProperty(JsonField.PROJECT_NAME)
  public DatasetPutMetadata.ProjectNameType getProjectName() {
    return this.projectName;
  }

  @JsonProperty(JsonField.PROJECT_NAME)
  public void setProjectName(DatasetPutMetadata.ProjectNameType projectName) {
    this.projectName = projectName;
  }

  @JsonProperty(JsonField.PROGRAM_NAME)
  public DatasetPutMetadata.ProgramNameType getProgramName() {
    return this.programName;
  }

  @JsonProperty(JsonField.PROGRAM_NAME)
  public void setProgramName(DatasetPutMetadata.ProgramNameType programName) {
    this.programName = programName;
  }

  @JsonProperty(JsonField.RELATED_STUDIES)
  public DatasetPutMetadata.RelatedStudiesType getRelatedStudies() {
    return this.relatedStudies;
  }

  @JsonProperty(JsonField.RELATED_STUDIES)
  public void setRelatedStudies(DatasetPutMetadata.RelatedStudiesType relatedStudies) {
    this.relatedStudies = relatedStudies;
  }

  @JsonProperty(JsonField.STUDY_CHARACTERISTICS)
  public StudyCharacteristicsPatch getStudyCharacteristics() {
    return this.studyCharacteristics;
  }

  @JsonProperty(JsonField.STUDY_CHARACTERISTICS)
  public void setStudyCharacteristics(StudyCharacteristicsPatch studyCharacteristics) {
    this.studyCharacteristics = studyCharacteristics;
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
  public DatasetPutMetadata.FundingType getFunding() {
    return this.funding;
  }

  @JsonProperty(JsonField.FUNDING)
  public void setFunding(DatasetPutMetadata.FundingType funding) {
    this.funding = funding;
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

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class SummaryTypeImpl implements DatasetPutMetadata.SummaryType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private String value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public String getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class FundingTypeImpl implements DatasetPutMetadata.FundingType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private List<DatasetFundingAward> value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public List<DatasetFundingAward> getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(List<DatasetFundingAward> value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class VisibilityTypeImpl implements DatasetPutMetadata.VisibilityType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private DatasetVisibility value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public DatasetVisibility getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(DatasetVisibility value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class ProgramNameTypeImpl implements DatasetPutMetadata.ProgramNameType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private String value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public String getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class NameTypeImpl implements DatasetPutMetadata.NameType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private String value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public String getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class DescriptionTypeImpl implements DatasetPutMetadata.DescriptionType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private String value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public String getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class TypeTypeImpl implements DatasetPutMetadata.TypeType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private DatasetTypeInput value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public DatasetTypeInput getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(DatasetTypeInput value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class ProjectNameTypeImpl implements DatasetPutMetadata.ProjectNameType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private String value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public String getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class ContactsTypeImpl implements DatasetPutMetadata.ContactsType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private List<DatasetContact> value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public List<DatasetContact> getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(List<DatasetContact> value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class RelatedStudiesTypeImpl implements DatasetPutMetadata.RelatedStudiesType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private List<RelatedStudy> value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public List<RelatedStudy> getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(List<RelatedStudy> value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class PublicationsTypeImpl implements DatasetPutMetadata.PublicationsType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private List<DatasetPublication> value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public List<DatasetPublication> getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(List<DatasetPublication> value) {
      this.value = value;
    }
  }
}
