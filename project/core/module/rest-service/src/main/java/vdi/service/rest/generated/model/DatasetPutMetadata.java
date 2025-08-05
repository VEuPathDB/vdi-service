package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetPutMetadataImpl.class
)
public interface DatasetPutMetadata {
  @JsonProperty(JsonField.ORIGIN)
  String getOrigin();

  @JsonProperty(JsonField.ORIGIN)
  void setOrigin(String origin);

  @JsonProperty(JsonField.REVISION_NOTE)
  String getRevisionNote();

  @JsonProperty(JsonField.REVISION_NOTE)
  void setRevisionNote(String revisionNote);

  @JsonProperty(JsonField.PATCH)
  List<JSONPatchAction> getPatch();

  @JsonProperty(JsonField.PATCH)
  void setPatch(List<JSONPatchAction> patch);
}
