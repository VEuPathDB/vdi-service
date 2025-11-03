package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = RevisionHistoryImpl.class
)
public interface RevisionHistory {
  @JsonProperty(JsonField.ORIGINAL_ID)
  String getOriginalId();

  @JsonProperty(JsonField.ORIGINAL_ID)
  void setOriginalId(String originalId);

  @JsonProperty(JsonField.REVISIONS)
  List<DatasetRevision> getRevisions();

  @JsonProperty(JsonField.REVISIONS)
  void setRevisions(List<DatasetRevision> revisions);
}
