package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetPatchRequestBodyImpl.class
)
public interface DatasetPatchRequestBody {
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

  @JsonProperty(JsonField.LINKED_DATASETS)
  LinkedDatasetsType getLinkedDatasets();

  @JsonProperty(JsonField.LINKED_DATASETS)
  void setLinkedDatasets(LinkedDatasetsType linkedDatasets);

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

  @JsonDeserialize(
      as = DatasetPatchRequestBodyImpl.SummaryTypeImpl.class
  )
  interface SummaryType {
    @JsonProperty("value")
    String getValue();

    @JsonProperty("value")
    void setValue(String value);
  }

  @JsonDeserialize(
      as = DatasetPatchRequestBodyImpl.FundingTypeImpl.class
  )
  interface FundingType {
    @JsonProperty("value")
    List<DatasetFundingAward> getValue();

    @JsonProperty("value")
    void setValue(List<DatasetFundingAward> value);
  }

  @JsonDeserialize(
      as = DatasetPatchRequestBodyImpl.VisibilityTypeImpl.class
  )
  interface VisibilityType {
    @JsonProperty("value")
    DatasetVisibility getValue();

    @JsonProperty("value")
    void setValue(DatasetVisibility value);
  }

  @JsonDeserialize(
      as = DatasetPatchRequestBodyImpl.ProgramNameTypeImpl.class
  )
  interface ProgramNameType {
    @JsonProperty("value")
    String getValue();

    @JsonProperty("value")
    void setValue(String value);
  }

  @JsonDeserialize(
      as = DatasetPatchRequestBodyImpl.NameTypeImpl.class
  )
  interface NameType {
    @JsonProperty("value")
    String getValue();

    @JsonProperty("value")
    void setValue(String value);
  }

  @JsonDeserialize(
      as = DatasetPatchRequestBodyImpl.DescriptionTypeImpl.class
  )
  interface DescriptionType {
    @JsonProperty("value")
    String getValue();

    @JsonProperty("value")
    void setValue(String value);
  }

  @JsonDeserialize(
      as = DatasetPatchRequestBodyImpl.TypeTypeImpl.class
  )
  interface TypeType {
    @JsonProperty("value")
    DatasetTypeInput getValue();

    @JsonProperty("value")
    void setValue(DatasetTypeInput value);
  }

  @JsonDeserialize(
      as = DatasetPatchRequestBodyImpl.ProjectNameTypeImpl.class
  )
  interface ProjectNameType {
    @JsonProperty("value")
    String getValue();

    @JsonProperty("value")
    void setValue(String value);
  }

  @JsonDeserialize(
      as = DatasetPatchRequestBodyImpl.ContactsTypeImpl.class
  )
  interface ContactsType {
    @JsonProperty("value")
    List<DatasetContact> getValue();

    @JsonProperty("value")
    void setValue(List<DatasetContact> value);
  }

  @JsonDeserialize(
      as = DatasetPatchRequestBodyImpl.LinkedDatasetsTypeImpl.class
  )
  interface LinkedDatasetsType {
    @JsonProperty("value")
    List<LinkedDataset> getValue();

    @JsonProperty("value")
    void setValue(List<LinkedDataset> value);
  }

  @JsonDeserialize(
      as = DatasetPatchRequestBodyImpl.PublicationsTypeImpl.class
  )
  interface PublicationsType {
    @JsonProperty("value")
    List<DatasetPublication> getValue();

    @JsonProperty("value")
    void setValue(List<DatasetPublication> value);
  }
}
