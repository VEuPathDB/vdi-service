package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = StudyCharacteristicsPatchImpl.class
)
public interface StudyCharacteristicsPatch {
  @JsonProperty(JsonField.STUDY_DESIGN)
  StudyDesignType getStudyDesign();

  @JsonProperty(JsonField.STUDY_DESIGN)
  void setStudyDesign(StudyDesignType studyDesign);

  @JsonProperty(JsonField.STUDY_TYPE)
  StudyTypeType getStudyType();

  @JsonProperty(JsonField.STUDY_TYPE)
  void setStudyType(StudyTypeType studyType);

  @JsonProperty(JsonField.COUNTRIES)
  CountriesType getCountries();

  @JsonProperty(JsonField.COUNTRIES)
  void setCountries(CountriesType countries);

  @JsonProperty(JsonField.YEARS)
  YearsType getYears();

  @JsonProperty(JsonField.YEARS)
  void setYears(YearsType years);

  @JsonProperty(JsonField.STUDY_SPECIES)
  StudySpeciesType getStudySpecies();

  @JsonProperty(JsonField.STUDY_SPECIES)
  void setStudySpecies(StudySpeciesType studySpecies);

  @JsonProperty(JsonField.DISEASES)
  DiseasesType getDiseases();

  @JsonProperty(JsonField.DISEASES)
  void setDiseases(DiseasesType diseases);

  @JsonProperty(JsonField.ASSOCIATED_FACTORS)
  AssociatedFactorsType getAssociatedFactors();

  @JsonProperty(JsonField.ASSOCIATED_FACTORS)
  void setAssociatedFactors(AssociatedFactorsType associatedFactors);

  @JsonProperty(JsonField.PARTICIPANT_AGES)
  ParticipantAgesType getParticipantAges();

  @JsonProperty(JsonField.PARTICIPANT_AGES)
  void setParticipantAges(ParticipantAgesType participantAges);

  @JsonProperty(JsonField.SAMPLE_TYPES)
  SampleTypesType getSampleTypes();

  @JsonProperty(JsonField.SAMPLE_TYPES)
  void setSampleTypes(SampleTypesType sampleTypes);

  @JsonDeserialize(
      as = StudyCharacteristicsPatchImpl.AssociatedFactorsTypeImpl.class
  )
  interface AssociatedFactorsType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    List<String> getValue();

    @JsonProperty("value")
    void setValue(List<String> value);
  }

  @JsonDeserialize(
      as = StudyCharacteristicsPatchImpl.StudyTypeTypeImpl.class
  )
  interface StudyTypeType extends PropertyPatch {
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
      as = StudyCharacteristicsPatchImpl.SampleTypesTypeImpl.class
  )
  interface SampleTypesType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    List<String> getValue();

    @JsonProperty("value")
    void setValue(List<String> value);
  }

  @JsonDeserialize(
      as = StudyCharacteristicsPatchImpl.StudyDesignTypeImpl.class
  )
  interface StudyDesignType extends PropertyPatch {
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
      as = StudyCharacteristicsPatchImpl.StudySpeciesTypeImpl.class
  )
  interface StudySpeciesType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    List<String> getValue();

    @JsonProperty("value")
    void setValue(List<String> value);
  }

  @JsonDeserialize(
      as = StudyCharacteristicsPatchImpl.DiseasesTypeImpl.class
  )
  interface DiseasesType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    List<String> getValue();

    @JsonProperty("value")
    void setValue(List<String> value);
  }

  @JsonDeserialize(
      as = StudyCharacteristicsPatchImpl.CountriesTypeImpl.class
  )
  interface CountriesType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    List<String> getValue();

    @JsonProperty("value")
    void setValue(List<String> value);
  }

  @JsonDeserialize(
      as = StudyCharacteristicsPatchImpl.ParticipantAgesTypeImpl.class
  )
  interface ParticipantAgesType extends PropertyPatch {
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
      as = StudyCharacteristicsPatchImpl.YearsTypeImpl.class
  )
  interface YearsType extends PropertyPatch {
    @JsonProperty("action")
    PatchAction getAction();

    @JsonProperty("action")
    void setAction(PatchAction action);

    @JsonProperty("value")
    SampleYearRange getValue();

    @JsonProperty("value")
    void setValue(SampleYearRange value);
  }
}
