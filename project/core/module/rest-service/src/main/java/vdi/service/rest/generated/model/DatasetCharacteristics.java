package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetCharacteristicsImpl.class
)
public interface DatasetCharacteristics {
  @JsonProperty(JsonField.STUDY_DESIGN)
  String getStudyDesign();

  @JsonProperty(JsonField.STUDY_DESIGN)
  void setStudyDesign(String studyDesign);

  @JsonProperty(JsonField.STUDY_TYPE)
  String getStudyType();

  @JsonProperty(JsonField.STUDY_TYPE)
  void setStudyType(String studyType);

  @JsonProperty(JsonField.COUNTRIES)
  List<String> getCountries();

  @JsonProperty(JsonField.COUNTRIES)
  void setCountries(List<String> countries);

  @JsonProperty(JsonField.YEARS)
  SampleYearRange getYears();

  @JsonProperty(JsonField.YEARS)
  void setYears(SampleYearRange years);

  @JsonProperty(JsonField.STUDY_SPECIES)
  List<String> getStudySpecies();

  @JsonProperty(JsonField.STUDY_SPECIES)
  void setStudySpecies(List<String> studySpecies);

  @JsonProperty(JsonField.DISEASES)
  List<String> getDiseases();

  @JsonProperty(JsonField.DISEASES)
  void setDiseases(List<String> diseases);

  @JsonProperty(JsonField.ASSOCIATED_FACTORS)
  List<String> getAssociatedFactors();

  @JsonProperty(JsonField.ASSOCIATED_FACTORS)
  void setAssociatedFactors(List<String> associatedFactors);

  @JsonProperty(JsonField.PARTICIPANT_AGES)
  String getParticipantAges();

  @JsonProperty(JsonField.PARTICIPANT_AGES)
  void setParticipantAges(String participantAges);

  @JsonProperty(JsonField.SAMPLE_TYPES)
  List<String> getSampleTypes();

  @JsonProperty(JsonField.SAMPLE_TYPES)
  void setSampleTypes(List<String> sampleTypes);
}
