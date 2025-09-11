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
public class DatasetCharacteristicsPatchImpl implements DatasetCharacteristicsPatch {
  @JsonProperty(JsonField.STUDY_DESIGN)
  private DatasetCharacteristicsPatch.StudyDesignType studyDesign;

  @JsonProperty(JsonField.STUDY_TYPE)
  private DatasetCharacteristicsPatch.StudyTypeType studyType;

  @JsonProperty(JsonField.COUNTRIES)
  private DatasetCharacteristicsPatch.CountriesType countries;

  @JsonProperty(JsonField.YEARS)
  private DatasetCharacteristicsPatch.YearsType years;

  @JsonProperty(JsonField.STUDY_SPECIES)
  private DatasetCharacteristicsPatch.StudySpeciesType studySpecies;

  @JsonProperty(JsonField.DISEASES)
  private DatasetCharacteristicsPatch.DiseasesType diseases;

  @JsonProperty(JsonField.ASSOCIATED_FACTORS)
  private DatasetCharacteristicsPatch.AssociatedFactorsType associatedFactors;

  @JsonProperty(JsonField.PARTICIPANT_AGES)
  private DatasetCharacteristicsPatch.ParticipantAgesType participantAges;

  @JsonProperty(JsonField.SAMPLE_TYPES)
  private DatasetCharacteristicsPatch.SampleTypesType sampleTypes;

  @JsonProperty(JsonField.STUDY_DESIGN)
  public DatasetCharacteristicsPatch.StudyDesignType getStudyDesign() {
    return this.studyDesign;
  }

  @JsonProperty(JsonField.STUDY_DESIGN)
  public void setStudyDesign(DatasetCharacteristicsPatch.StudyDesignType studyDesign) {
    this.studyDesign = studyDesign;
  }

  @JsonProperty(JsonField.STUDY_TYPE)
  public DatasetCharacteristicsPatch.StudyTypeType getStudyType() {
    return this.studyType;
  }

  @JsonProperty(JsonField.STUDY_TYPE)
  public void setStudyType(DatasetCharacteristicsPatch.StudyTypeType studyType) {
    this.studyType = studyType;
  }

  @JsonProperty(JsonField.COUNTRIES)
  public DatasetCharacteristicsPatch.CountriesType getCountries() {
    return this.countries;
  }

  @JsonProperty(JsonField.COUNTRIES)
  public void setCountries(DatasetCharacteristicsPatch.CountriesType countries) {
    this.countries = countries;
  }

  @JsonProperty(JsonField.YEARS)
  public DatasetCharacteristicsPatch.YearsType getYears() {
    return this.years;
  }

  @JsonProperty(JsonField.YEARS)
  public void setYears(DatasetCharacteristicsPatch.YearsType years) {
    this.years = years;
  }

  @JsonProperty(JsonField.STUDY_SPECIES)
  public DatasetCharacteristicsPatch.StudySpeciesType getStudySpecies() {
    return this.studySpecies;
  }

  @JsonProperty(JsonField.STUDY_SPECIES)
  public void setStudySpecies(DatasetCharacteristicsPatch.StudySpeciesType studySpecies) {
    this.studySpecies = studySpecies;
  }

  @JsonProperty(JsonField.DISEASES)
  public DatasetCharacteristicsPatch.DiseasesType getDiseases() {
    return this.diseases;
  }

  @JsonProperty(JsonField.DISEASES)
  public void setDiseases(DatasetCharacteristicsPatch.DiseasesType diseases) {
    this.diseases = diseases;
  }

  @JsonProperty(JsonField.ASSOCIATED_FACTORS)
  public DatasetCharacteristicsPatch.AssociatedFactorsType getAssociatedFactors() {
    return this.associatedFactors;
  }

  @JsonProperty(JsonField.ASSOCIATED_FACTORS)
  public void setAssociatedFactors(
      DatasetCharacteristicsPatch.AssociatedFactorsType associatedFactors) {
    this.associatedFactors = associatedFactors;
  }

  @JsonProperty(JsonField.PARTICIPANT_AGES)
  public DatasetCharacteristicsPatch.ParticipantAgesType getParticipantAges() {
    return this.participantAges;
  }

  @JsonProperty(JsonField.PARTICIPANT_AGES)
  public void setParticipantAges(DatasetCharacteristicsPatch.ParticipantAgesType participantAges) {
    this.participantAges = participantAges;
  }

  @JsonProperty(JsonField.SAMPLE_TYPES)
  public DatasetCharacteristicsPatch.SampleTypesType getSampleTypes() {
    return this.sampleTypes;
  }

  @JsonProperty(JsonField.SAMPLE_TYPES)
  public void setSampleTypes(DatasetCharacteristicsPatch.SampleTypesType sampleTypes) {
    this.sampleTypes = sampleTypes;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder("value")
  public static class AssociatedFactorsTypeImpl implements DatasetCharacteristicsPatch.AssociatedFactorsType {
    @JsonProperty("value")
    private List<String> value;

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
  @JsonPropertyOrder("value")
  public static class StudyTypeTypeImpl implements DatasetCharacteristicsPatch.StudyTypeType {
    @JsonProperty("value")
    private String value;

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
  @JsonPropertyOrder("value")
  public static class SampleTypesTypeImpl implements DatasetCharacteristicsPatch.SampleTypesType {
    @JsonProperty("value")
    private List<String> value;

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
  @JsonPropertyOrder("value")
  public static class StudyDesignTypeImpl implements DatasetCharacteristicsPatch.StudyDesignType {
    @JsonProperty("value")
    private String value;

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
  @JsonPropertyOrder("value")
  public static class StudySpeciesTypeImpl implements DatasetCharacteristicsPatch.StudySpeciesType {
    @JsonProperty("value")
    private List<String> value;

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
  @JsonPropertyOrder("value")
  public static class DiseasesTypeImpl implements DatasetCharacteristicsPatch.DiseasesType {
    @JsonProperty("value")
    private List<String> value;

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
  @JsonPropertyOrder("value")
  public static class CountriesTypeImpl implements DatasetCharacteristicsPatch.CountriesType {
    @JsonProperty("value")
    private List<String> value;

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
  @JsonPropertyOrder("value")
  public static class ParticipantAgesTypeImpl implements DatasetCharacteristicsPatch.ParticipantAgesType {
    @JsonProperty("value")
    private String value;

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
  @JsonPropertyOrder("value")
  public static class YearsTypeImpl implements DatasetCharacteristicsPatch.YearsType {
    @JsonProperty("value")
    private SampleYearRange value;

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
