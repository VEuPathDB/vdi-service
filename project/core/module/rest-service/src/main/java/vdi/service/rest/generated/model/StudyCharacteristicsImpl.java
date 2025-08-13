package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "studyDesign",
    "studyType",
    "countries",
    "years",
    "studySpecies",
    "diseases",
    "associatedFactors",
    "participantAges",
    "sampleTypes"
})
public class StudyCharacteristicsImpl implements StudyCharacteristics {
  @JsonProperty(JsonField.STUDY_DESIGN)
  private String studyDesign;

  @JsonProperty(JsonField.STUDY_TYPE)
  private String studyType;

  @JsonProperty(JsonField.COUNTRIES)
  private List<String> countries;

  @JsonProperty(JsonField.YEARS)
  private SampleYearRange years;

  @JsonProperty(JsonField.STUDY_SPECIES)
  private List<String> studySpecies;

  @JsonProperty(JsonField.DISEASES)
  private List<String> diseases;

  @JsonProperty(JsonField.ASSOCIATED_FACTORS)
  private List<String> associatedFactors;

  @JsonProperty(JsonField.PARTICIPANT_AGES)
  private String participantAges;

  @JsonProperty(JsonField.SAMPLE_TYPES)
  private List<String> sampleTypes;

  @JsonProperty(JsonField.STUDY_DESIGN)
  public String getStudyDesign() {
    return this.studyDesign;
  }

  @JsonProperty(JsonField.STUDY_DESIGN)
  public void setStudyDesign(String studyDesign) {
    this.studyDesign = studyDesign;
  }

  @JsonProperty(JsonField.STUDY_TYPE)
  public String getStudyType() {
    return this.studyType;
  }

  @JsonProperty(JsonField.STUDY_TYPE)
  public void setStudyType(String studyType) {
    this.studyType = studyType;
  }

  @JsonProperty(JsonField.COUNTRIES)
  public List<String> getCountries() {
    return this.countries;
  }

  @JsonProperty(JsonField.COUNTRIES)
  public void setCountries(List<String> countries) {
    this.countries = countries;
  }

  @JsonProperty(JsonField.YEARS)
  public SampleYearRange getYears() {
    return this.years;
  }

  @JsonProperty(JsonField.YEARS)
  public void setYears(SampleYearRange years) {
    this.years = years;
  }

  @JsonProperty(JsonField.STUDY_SPECIES)
  public List<String> getStudySpecies() {
    return this.studySpecies;
  }

  @JsonProperty(JsonField.STUDY_SPECIES)
  public void setStudySpecies(List<String> studySpecies) {
    this.studySpecies = studySpecies;
  }

  @JsonProperty(JsonField.DISEASES)
  public List<String> getDiseases() {
    return this.diseases;
  }

  @JsonProperty(JsonField.DISEASES)
  public void setDiseases(List<String> diseases) {
    this.diseases = diseases;
  }

  @JsonProperty(JsonField.ASSOCIATED_FACTORS)
  public List<String> getAssociatedFactors() {
    return this.associatedFactors;
  }

  @JsonProperty(JsonField.ASSOCIATED_FACTORS)
  public void setAssociatedFactors(List<String> associatedFactors) {
    this.associatedFactors = associatedFactors;
  }

  @JsonProperty(JsonField.PARTICIPANT_AGES)
  public String getParticipantAges() {
    return this.participantAges;
  }

  @JsonProperty(JsonField.PARTICIPANT_AGES)
  public void setParticipantAges(String participantAges) {
    this.participantAges = participantAges;
  }

  @JsonProperty(JsonField.SAMPLE_TYPES)
  public List<String> getSampleTypes() {
    return this.sampleTypes;
  }

  @JsonProperty(JsonField.SAMPLE_TYPES)
  public void setSampleTypes(List<String> sampleTypes) {
    this.sampleTypes = sampleTypes;
  }
}
