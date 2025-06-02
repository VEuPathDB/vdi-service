package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "action",
    "timestamp",
    "revisionId",
    "revisionNote",
    "fileListUrl"
})
public class DatasetRevisionImpl implements DatasetRevision {
  @JsonProperty(JsonField.ACTION)
  private DatasetRevisionAction action;

  @JsonProperty(JsonField.TIMESTAMP)
  private OffsetDateTime timestamp;

  @JsonProperty(JsonField.REVISION_ID)
  private String revisionId;

  @JsonProperty(JsonField.REVISION_NOTE)
  private String revisionNote;

  @JsonProperty(JsonField.FILE_LIST_URL)
  private String fileListUrl;

  @JsonProperty(JsonField.ACTION)
  public DatasetRevisionAction getAction() {
    return this.action;
  }

  @JsonProperty(JsonField.ACTION)
  public void setAction(DatasetRevisionAction action) {
    this.action = action;
  }

  @JsonProperty(JsonField.TIMESTAMP)
  public OffsetDateTime getTimestamp() {
    return this.timestamp;
  }

  @JsonProperty(JsonField.TIMESTAMP)
  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }

  @JsonProperty(JsonField.REVISION_ID)
  public String getRevisionId() {
    return this.revisionId;
  }

  @JsonProperty(JsonField.REVISION_ID)
  public void setRevisionId(String revisionId) {
    this.revisionId = revisionId;
  }

  @JsonProperty(JsonField.REVISION_NOTE)
  public String getRevisionNote() {
    return this.revisionNote;
  }

  @JsonProperty(JsonField.REVISION_NOTE)
  public void setRevisionNote(String revisionNote) {
    this.revisionNote = revisionNote;
  }

  @JsonProperty(JsonField.FILE_LIST_URL)
  public String getFileListUrl() {
    return this.fileListUrl;
  }

  @JsonProperty(JsonField.FILE_LIST_URL)
  public void setFileListUrl(String fileListUrl) {
    this.fileListUrl = fileListUrl;
  }
}
