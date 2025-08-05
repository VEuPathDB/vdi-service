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
    "years"
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
}
