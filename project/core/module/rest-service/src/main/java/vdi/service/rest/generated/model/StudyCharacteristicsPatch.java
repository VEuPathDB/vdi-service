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
  interface AssociatedFactorsType {
    @JsonProperty("value")
    List<String> getValue();

    @JsonProperty("value")
    void setValue(List<String> value);
  }

  @JsonDeserialize(
      as = StudyCharacteristicsPatchImpl.StudyTypeTypeImpl.class
  )
  interface StudyTypeType {
    @JsonProperty("value")
    String getValue();

    @JsonProperty("value")
    void setValue(String value);
  }

  @JsonDeserialize(
      as = StudyCharacteristicsPatchImpl.SampleTypesTypeImpl.class
  )
  interface SampleTypesType {
    @JsonProperty("value")
    List<String> getValue();

    @JsonProperty("value")
    void setValue(List<String> value);
  }

  @JsonDeserialize(
      as = StudyCharacteristicsPatchImpl.StudyDesignTypeImpl.class
  )
  interface StudyDesignType {
    @JsonProperty("value")
    String getValue();

    @JsonProperty("value")
    void setValue(String value);
  }

  @JsonDeserialize(
      as = StudyCharacteristicsPatchImpl.StudySpeciesTypeImpl.class
  )
  interface StudySpeciesType {
    @JsonProperty("value")
    List<String> getValue();

    @JsonProperty("value")
    void setValue(List<String> value);
  }

  @JsonDeserialize(
      as = StudyCharacteristicsPatchImpl.DiseasesTypeImpl.class
  )
  interface DiseasesType {
    @JsonProperty("value")
    List<String> getValue();

    @JsonProperty("value")
    void setValue(List<String> value);
  }

  @JsonDeserialize(
      as = StudyCharacteristicsPatchImpl.CountriesTypeImpl.class
  )
  interface CountriesType {
    @JsonProperty("value")
    List<String> getValue();

    @JsonProperty("value")
    void setValue(List<String> value);
  }

  @JsonDeserialize(
      as = StudyCharacteristicsPatchImpl.ParticipantAgesTypeImpl.class
  )
  interface ParticipantAgesType {
    @JsonProperty("value")
    String getValue();

    @JsonProperty("value")
    void setValue(String value);
  }

  @JsonDeserialize(
      as = StudyCharacteristicsPatchImpl.YearsTypeImpl.class
  )
  interface YearsType {
    @JsonProperty("value")
    SampleYearRange getValue();

    @JsonProperty("value")
    void setValue(SampleYearRange value);
  }
}
