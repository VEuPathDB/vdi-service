package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "originalId",
    "revisions"
})
public class RevisionHistoryImpl implements RevisionHistory {
  @JsonProperty(JsonField.ORIGINAL_ID)
  private String originalId;

  @JsonProperty(JsonField.REVISIONS)
  private List<DatasetRevision> revisions;

  @JsonProperty(JsonField.ORIGINAL_ID)
  public String getOriginalId() {
    return this.originalId;
  }

  @JsonProperty(JsonField.ORIGINAL_ID)
  public void setOriginalId(String originalId) {
    this.originalId = originalId;
  }

  @JsonProperty(JsonField.REVISIONS)
  public List<DatasetRevision> getRevisions() {
    return this.revisions;
  }

  @JsonProperty(JsonField.REVISIONS)
  public void setRevisions(List<DatasetRevision> revisions) {
    this.revisions = revisions;
  }
}
