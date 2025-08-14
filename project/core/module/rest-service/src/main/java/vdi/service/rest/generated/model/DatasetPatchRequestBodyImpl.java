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
    "funding"
})
public class DatasetPatchRequestBodyImpl implements DatasetPatchRequestBody {
  @JsonProperty(JsonField.TYPE)
  private DatasetPatchRequestBody.TypeType type;

  @JsonProperty(JsonField.VISIBILITY)
  private DatasetPatchRequestBody.VisibilityType visibility;

  @JsonProperty(JsonField.NAME)
  private DatasetPatchRequestBody.NameType name;

  @JsonProperty(JsonField.SUMMARY)
  private DatasetPatchRequestBody.SummaryType summary;

  @JsonProperty(JsonField.DESCRIPTION)
  private DatasetPatchRequestBody.DescriptionType description;

  @JsonProperty(JsonField.PUBLICATIONS)
  private DatasetPatchRequestBody.PublicationsType publications;

  @JsonProperty(JsonField.CONTACTS)
  private DatasetPatchRequestBody.ContactsType contacts;

  @JsonProperty(JsonField.PROJECT_NAME)
  private DatasetPatchRequestBody.ProjectNameType projectName;

  @JsonProperty(JsonField.PROGRAM_NAME)
  private DatasetPatchRequestBody.ProgramNameType programName;

  @JsonProperty(JsonField.RELATED_STUDIES)
  private DatasetPatchRequestBody.RelatedStudiesType relatedStudies;

  @JsonProperty(JsonField.STUDY_CHARACTERISTICS)
  private StudyCharacteristicsPatch studyCharacteristics;

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  private ExternalIdentifiersPatch externalIdentifiers;

  @JsonProperty(JsonField.FUNDING)
  private DatasetPatchRequestBody.FundingType funding;

  @JsonProperty(JsonField.TYPE)
  public DatasetPatchRequestBody.TypeType getType() {
    return this.type;
  }

  @JsonProperty(JsonField.TYPE)
  public void setType(DatasetPatchRequestBody.TypeType type) {
    this.type = type;
  }

  @JsonProperty(JsonField.VISIBILITY)
  public DatasetPatchRequestBody.VisibilityType getVisibility() {
    return this.visibility;
  }

  @JsonProperty(JsonField.VISIBILITY)
  public void setVisibility(DatasetPatchRequestBody.VisibilityType visibility) {
    this.visibility = visibility;
  }

  @JsonProperty(JsonField.NAME)
  public DatasetPatchRequestBody.NameType getName() {
    return this.name;
  }

  @JsonProperty(JsonField.NAME)
  public void setName(DatasetPatchRequestBody.NameType name) {
    this.name = name;
  }

  @JsonProperty(JsonField.SUMMARY)
  public DatasetPatchRequestBody.SummaryType getSummary() {
    return this.summary;
  }

  @JsonProperty(JsonField.SUMMARY)
  public void setSummary(DatasetPatchRequestBody.SummaryType summary) {
    this.summary = summary;
  }

  @JsonProperty(JsonField.DESCRIPTION)
  public DatasetPatchRequestBody.DescriptionType getDescription() {
    return this.description;
  }

  @JsonProperty(JsonField.DESCRIPTION)
  public void setDescription(DatasetPatchRequestBody.DescriptionType description) {
    this.description = description;
  }

  @JsonProperty(JsonField.PUBLICATIONS)
  public DatasetPatchRequestBody.PublicationsType getPublications() {
    return this.publications;
  }

  @JsonProperty(JsonField.PUBLICATIONS)
  public void setPublications(DatasetPatchRequestBody.PublicationsType publications) {
    this.publications = publications;
  }

  @JsonProperty(JsonField.CONTACTS)
  public DatasetPatchRequestBody.ContactsType getContacts() {
    return this.contacts;
  }

  @JsonProperty(JsonField.CONTACTS)
  public void setContacts(DatasetPatchRequestBody.ContactsType contacts) {
    this.contacts = contacts;
  }

  @JsonProperty(JsonField.PROJECT_NAME)
  public DatasetPatchRequestBody.ProjectNameType getProjectName() {
    return this.projectName;
  }

  @JsonProperty(JsonField.PROJECT_NAME)
  public void setProjectName(DatasetPatchRequestBody.ProjectNameType projectName) {
    this.projectName = projectName;
  }

  @JsonProperty(JsonField.PROGRAM_NAME)
  public DatasetPatchRequestBody.ProgramNameType getProgramName() {
    return this.programName;
  }

  @JsonProperty(JsonField.PROGRAM_NAME)
  public void setProgramName(DatasetPatchRequestBody.ProgramNameType programName) {
    this.programName = programName;
  }

  @JsonProperty(JsonField.RELATED_STUDIES)
  public DatasetPatchRequestBody.RelatedStudiesType getRelatedStudies() {
    return this.relatedStudies;
  }

  @JsonProperty(JsonField.RELATED_STUDIES)
  public void setRelatedStudies(DatasetPatchRequestBody.RelatedStudiesType relatedStudies) {
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
  public DatasetPatchRequestBody.FundingType getFunding() {
    return this.funding;
  }

  @JsonProperty(JsonField.FUNDING)
  public void setFunding(DatasetPatchRequestBody.FundingType funding) {
    this.funding = funding;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class SummaryTypeImpl implements DatasetPatchRequestBody.SummaryType {
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
  public static class FundingTypeImpl implements DatasetPatchRequestBody.FundingType {
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
  public static class VisibilityTypeImpl implements DatasetPatchRequestBody.VisibilityType {
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
  public static class ProgramNameTypeImpl implements DatasetPatchRequestBody.ProgramNameType {
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
  public static class NameTypeImpl implements DatasetPatchRequestBody.NameType {
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
  public static class DescriptionTypeImpl implements DatasetPatchRequestBody.DescriptionType {
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
  public static class TypeTypeImpl implements DatasetPatchRequestBody.TypeType {
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
  public static class ProjectNameTypeImpl implements DatasetPatchRequestBody.ProjectNameType {
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
  public static class ContactsTypeImpl implements DatasetPatchRequestBody.ContactsType {
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
  public static class RelatedStudiesTypeImpl implements DatasetPatchRequestBody.RelatedStudiesType {
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
  public static class PublicationsTypeImpl implements DatasetPatchRequestBody.PublicationsType {
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
