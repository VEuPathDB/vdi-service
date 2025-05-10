package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.OffsetDateTime;

@JsonDeserialize(
    as = DatasetRevisionImpl.class
)
public interface DatasetRevision {
  @JsonProperty(JsonField.ACTION)
  DatasetRevisionAction getAction();

  @JsonProperty(JsonField.ACTION)
  void setAction(DatasetRevisionAction action);

  @JsonProperty(JsonField.TIMESTAMP)
  OffsetDateTime getTimestamp();

  @JsonProperty(JsonField.TIMESTAMP)
  void setTimestamp(OffsetDateTime timestamp);

  @JsonProperty(JsonField.REVISION_ID)
  String getRevisionId();

  @JsonProperty(JsonField.REVISION_ID)
  void setRevisionId(String revisionId);

  @JsonProperty(JsonField.REVISION_NOTE)
  String getRevisionNote();

  @JsonProperty(JsonField.REVISION_NOTE)
  void setRevisionNote(String revisionNote);

  @JsonProperty(JsonField.FILE_LIST_URL)
  String getFileListUrl();

  @JsonProperty(JsonField.FILE_LIST_URL)
  void setFileListUrl(String fileListUrl);
}
