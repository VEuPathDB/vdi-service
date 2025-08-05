package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = StudyCharacteristicsImpl.class
)
public interface StudyCharacteristics {
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
}
