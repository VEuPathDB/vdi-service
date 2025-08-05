package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = RelatedStudyImpl.class
)
public interface RelatedStudy {
  @JsonProperty(JsonField.STUDY_URI)
  String getStudyUri();

  @JsonProperty(JsonField.STUDY_URI)
  void setStudyUri(String studyUri);

  @JsonProperty(JsonField.SHARES_RECORDS)
  boolean getSharesRecords();

  @JsonProperty(JsonField.SHARES_RECORDS)
  void setSharesRecords(boolean sharesRecords);
}
