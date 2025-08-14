package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetPutMetadataImpl.class
)
public interface DatasetPutMetadata extends DatasetPatchRequestBody {
  @JsonProperty(JsonField.TYPE)
  TypeType getType();

  @JsonProperty(JsonField.TYPE)
  void setType(TypeType type);

  @JsonProperty(JsonField.VISIBILITY)
  VisibilityType getVisibility();

  @JsonProperty(JsonField.VISIBILITY)
  void setVisibility(VisibilityType visibility);

  @JsonProperty(JsonField.NAME)
  NameType getName();

  @JsonProperty(JsonField.NAME)
  void setName(NameType name);

  @JsonProperty(JsonField.SUMMARY)
  SummaryType getSummary();

  @JsonProperty(JsonField.SUMMARY)
  void setSummary(SummaryType summary);

  @JsonProperty(JsonField.DESCRIPTION)
  DescriptionType getDescription();

  @JsonProperty(JsonField.DESCRIPTION)
  void setDescription(DescriptionType description);

  @JsonProperty(JsonField.PUBLICATIONS)
  PublicationsType getPublications();

  @JsonProperty(JsonField.PUBLICATIONS)
  void setPublications(PublicationsType publications);

  @JsonProperty(JsonField.CONTACTS)
  ContactsType getContacts();

  @JsonProperty(JsonField.CONTACTS)
  void setContacts(ContactsType contacts);

  @JsonProperty(JsonField.PROJECT_NAME)
  ProjectNameType getProjectName();

  @JsonProperty(JsonField.PROJECT_NAME)
  void setProjectName(ProjectNameType projectName);

  @JsonProperty(JsonField.PROGRAM_NAME)
  ProgramNameType getProgramName();

  @JsonProperty(JsonField.PROGRAM_NAME)
  void setProgramName(ProgramNameType programName);

  @JsonProperty(JsonField.RELATED_STUDIES)
  RelatedStudiesType getRelatedStudies();

  @JsonProperty(JsonField.RELATED_STUDIES)
  void setRelatedStudies(RelatedStudiesType relatedStudies);

  @JsonProperty(JsonField.STUDY_CHARACTERISTICS)
  StudyCharacteristicsPatch getStudyCharacteristics();

  @JsonProperty(JsonField.STUDY_CHARACTERISTICS)
  void setStudyCharacteristics(StudyCharacteristicsPatch studyCharacteristics);

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  ExternalIdentifiersPatch getExternalIdentifiers();

  @JsonProperty(JsonField.EXTERNAL_IDENTIFIERS)
  void setExternalIdentifiers(ExternalIdentifiersPatch externalIdentifiers);

  @JsonProperty(JsonField.FUNDING)
  FundingType getFunding();

  @JsonProperty(JsonField.FUNDING)
  void setFunding(FundingType funding);

  @JsonProperty(JsonField.ORIGIN)
  String getOrigin();

  @JsonProperty(JsonField.ORIGIN)
  void setOrigin(String origin);

  @JsonProperty(JsonField.REVISION_NOTE)
  String getRevisionNote();

  @JsonProperty(JsonField.REVISION_NOTE)
  void setRevisionNote(String revisionNote);

  @JsonDeserialize(
      as = DatasetPutMetadataImpl.SummaryTypeImpl.class
  )
  interface SummaryType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    String getValue();

    @JsonProperty("value")
    void setValue(String value);
  }

  @JsonDeserialize(
      as = DatasetPutMetadataImpl.FundingTypeImpl.class
  )
  interface FundingType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    List<DatasetFundingAward> getValue();

    @JsonProperty("value")
    void setValue(List<DatasetFundingAward> value);
  }

  @JsonDeserialize(
      as = DatasetPutMetadataImpl.VisibilityTypeImpl.class
  )
  interface VisibilityType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    DatasetVisibility getValue();

    @JsonProperty("value")
    void setValue(DatasetVisibility value);
  }

  @JsonDeserialize(
      as = DatasetPutMetadataImpl.ProgramNameTypeImpl.class
  )
  interface ProgramNameType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    String getValue();

    @JsonProperty("value")
    void setValue(String value);
  }

  @JsonDeserialize(
      as = DatasetPutMetadataImpl.NameTypeImpl.class
  )
  interface NameType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    String getValue();

    @JsonProperty("value")
    void setValue(String value);
  }

  @JsonDeserialize(
      as = DatasetPutMetadataImpl.DescriptionTypeImpl.class
  )
  interface DescriptionType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    String getValue();

    @JsonProperty("value")
    void setValue(String value);
  }

  @JsonDeserialize(
      as = DatasetPutMetadataImpl.TypeTypeImpl.class
  )
  interface TypeType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    DatasetTypeInput getValue();

    @JsonProperty("value")
    void setValue(DatasetTypeInput value);
  }

  @JsonDeserialize(
      as = DatasetPutMetadataImpl.ProjectNameTypeImpl.class
  )
  interface ProjectNameType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    String getValue();

    @JsonProperty("value")
    void setValue(String value);
  }

  @JsonDeserialize(
      as = DatasetPutMetadataImpl.ContactsTypeImpl.class
  )
  interface ContactsType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    List<DatasetContact> getValue();

    @JsonProperty("value")
    void setValue(List<DatasetContact> value);
  }

  @JsonDeserialize(
      as = DatasetPutMetadataImpl.RelatedStudiesTypeImpl.class
  )
  interface RelatedStudiesType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    List<RelatedStudy> getValue();

    @JsonProperty("value")
    void setValue(List<RelatedStudy> value);
  }

  @JsonDeserialize(
      as = DatasetPutMetadataImpl.PublicationsTypeImpl.class
  )
  interface PublicationsType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    List<DatasetPublication> getValue();

    @JsonProperty("value")
    void setValue(List<DatasetPublication> value);
  }
}
