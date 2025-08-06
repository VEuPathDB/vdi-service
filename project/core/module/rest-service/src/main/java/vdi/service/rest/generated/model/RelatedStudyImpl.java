package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "studyUri",
    "sharesRecords"
})
public class RelatedStudyImpl implements RelatedStudy {
  @JsonProperty(JsonField.STUDY_URI)
  private String studyUri;

  @JsonProperty(JsonField.SHARES_RECORDS)
  private Boolean sharesRecords;

  @JsonProperty(JsonField.STUDY_URI)
  public String getStudyUri() {
    return this.studyUri;
  }

  @JsonProperty(JsonField.STUDY_URI)
  public void setStudyUri(String studyUri) {
    this.studyUri = studyUri;
  }

  @JsonProperty(JsonField.SHARES_RECORDS)
  public Boolean getSharesRecords() {
    return this.sharesRecords;
  }

  @JsonProperty(JsonField.SHARES_RECORDS)
  public void setSharesRecords(Boolean sharesRecords) {
    this.sharesRecords = sharesRecords;
  }
}
