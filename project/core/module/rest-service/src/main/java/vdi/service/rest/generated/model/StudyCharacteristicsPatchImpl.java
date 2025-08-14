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
public class StudyCharacteristicsPatchImpl implements StudyCharacteristicsPatch {
  @JsonProperty(JsonField.STUDY_DESIGN)
  private StudyCharacteristicsPatch.StudyDesignType studyDesign;

  @JsonProperty(JsonField.STUDY_TYPE)
  private StudyCharacteristicsPatch.StudyTypeType studyType;

  @JsonProperty(JsonField.COUNTRIES)
  private StudyCharacteristicsPatch.CountriesType countries;

  @JsonProperty(JsonField.YEARS)
  private StudyCharacteristicsPatch.YearsType years;

  @JsonProperty(JsonField.STUDY_SPECIES)
  private StudyCharacteristicsPatch.StudySpeciesType studySpecies;

  @JsonProperty(JsonField.DISEASES)
  private StudyCharacteristicsPatch.DiseasesType diseases;

  @JsonProperty(JsonField.ASSOCIATED_FACTORS)
  private StudyCharacteristicsPatch.AssociatedFactorsType associatedFactors;

  @JsonProperty(JsonField.PARTICIPANT_AGES)
  private StudyCharacteristicsPatch.ParticipantAgesType participantAges;

  @JsonProperty(JsonField.SAMPLE_TYPES)
  private StudyCharacteristicsPatch.SampleTypesType sampleTypes;

  @JsonProperty(JsonField.STUDY_DESIGN)
  public StudyCharacteristicsPatch.StudyDesignType getStudyDesign() {
    return this.studyDesign;
  }

  @JsonProperty(JsonField.STUDY_DESIGN)
  public void setStudyDesign(StudyCharacteristicsPatch.StudyDesignType studyDesign) {
    this.studyDesign = studyDesign;
  }

  @JsonProperty(JsonField.STUDY_TYPE)
  public StudyCharacteristicsPatch.StudyTypeType getStudyType() {
    return this.studyType;
  }

  @JsonProperty(JsonField.STUDY_TYPE)
  public void setStudyType(StudyCharacteristicsPatch.StudyTypeType studyType) {
    this.studyType = studyType;
  }

  @JsonProperty(JsonField.COUNTRIES)
  public StudyCharacteristicsPatch.CountriesType getCountries() {
    return this.countries;
  }

  @JsonProperty(JsonField.COUNTRIES)
  public void setCountries(StudyCharacteristicsPatch.CountriesType countries) {
    this.countries = countries;
  }

  @JsonProperty(JsonField.YEARS)
  public StudyCharacteristicsPatch.YearsType getYears() {
    return this.years;
  }

  @JsonProperty(JsonField.YEARS)
  public void setYears(StudyCharacteristicsPatch.YearsType years) {
    this.years = years;
  }

  @JsonProperty(JsonField.STUDY_SPECIES)
  public StudyCharacteristicsPatch.StudySpeciesType getStudySpecies() {
    return this.studySpecies;
  }

  @JsonProperty(JsonField.STUDY_SPECIES)
  public void setStudySpecies(StudyCharacteristicsPatch.StudySpeciesType studySpecies) {
    this.studySpecies = studySpecies;
  }

  @JsonProperty(JsonField.DISEASES)
  public StudyCharacteristicsPatch.DiseasesType getDiseases() {
    return this.diseases;
  }

  @JsonProperty(JsonField.DISEASES)
  public void setDiseases(StudyCharacteristicsPatch.DiseasesType diseases) {
    this.diseases = diseases;
  }

  @JsonProperty(JsonField.ASSOCIATED_FACTORS)
  public StudyCharacteristicsPatch.AssociatedFactorsType getAssociatedFactors() {
    return this.associatedFactors;
  }

  @JsonProperty(JsonField.ASSOCIATED_FACTORS)
  public void setAssociatedFactors(
      StudyCharacteristicsPatch.AssociatedFactorsType associatedFactors) {
    this.associatedFactors = associatedFactors;
  }

  @JsonProperty(JsonField.PARTICIPANT_AGES)
  public StudyCharacteristicsPatch.ParticipantAgesType getParticipantAges() {
    return this.participantAges;
  }

  @JsonProperty(JsonField.PARTICIPANT_AGES)
  public void setParticipantAges(StudyCharacteristicsPatch.ParticipantAgesType participantAges) {
    this.participantAges = participantAges;
  }

  @JsonProperty(JsonField.SAMPLE_TYPES)
  public StudyCharacteristicsPatch.SampleTypesType getSampleTypes() {
    return this.sampleTypes;
  }

  @JsonProperty(JsonField.SAMPLE_TYPES)
  public void setSampleTypes(StudyCharacteristicsPatch.SampleTypesType sampleTypes) {
    this.sampleTypes = sampleTypes;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class AssociatedFactorsTypeImpl implements StudyCharacteristicsPatch.AssociatedFactorsType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private List<String> value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public List<String> getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(List<String> value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class StudyTypeTypeImpl implements StudyCharacteristicsPatch.StudyTypeType {
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
  public static class SampleTypesTypeImpl implements StudyCharacteristicsPatch.SampleTypesType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private List<String> value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public List<String> getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(List<String> value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class StudyDesignTypeImpl implements StudyCharacteristicsPatch.StudyDesignType {
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
  public static class StudySpeciesTypeImpl implements StudyCharacteristicsPatch.StudySpeciesType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private List<String> value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public List<String> getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(List<String> value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class DiseasesTypeImpl implements StudyCharacteristicsPatch.DiseasesType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private List<String> value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public List<String> getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(List<String> value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class CountriesTypeImpl implements StudyCharacteristicsPatch.CountriesType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private List<String> value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public List<String> getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(List<String> value) {
      this.value = value;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "action",
      "value"
  })
  public static class ParticipantAgesTypeImpl implements StudyCharacteristicsPatch.ParticipantAgesType {
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
  public static class YearsTypeImpl implements StudyCharacteristicsPatch.YearsType {
    @JsonProperty("action")
    private PatchAction action;

    @JsonProperty("value")
    private SampleYearRange value;

    @JsonProperty("action")
    public PatchAction getAction() {
      return this.action;
    }

    @JsonProperty("action")
    public void setAction(PatchAction action) {
      this.action = action;
    }

    @JsonProperty("value")
    public SampleYearRange getValue() {
      return this.value;
    }

    @JsonProperty("value")
    public void setValue(SampleYearRange value) {
      this.value = value;
    }
  }
}
