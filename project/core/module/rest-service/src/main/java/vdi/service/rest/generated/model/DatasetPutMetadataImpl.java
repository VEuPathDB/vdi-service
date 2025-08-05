package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "origin",
    "revisionNote",
    "patch"
})
public class DatasetPutMetadataImpl implements DatasetPutMetadata {
  @JsonProperty(JsonField.ORIGIN)
  private String origin;

  @JsonProperty(JsonField.REVISION_NOTE)
  private String revisionNote;

  @JsonProperty(JsonField.PATCH)
  private List<JSONPatchAction> patch;

  @JsonProperty(JsonField.ORIGIN)
  public String getOrigin() {
    return this.origin;
  }

  @JsonProperty(JsonField.ORIGIN)
  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @JsonProperty(JsonField.REVISION_NOTE)
  public String getRevisionNote() {
    return this.revisionNote;
  }

  @JsonProperty(JsonField.REVISION_NOTE)
  public void setRevisionNote(String revisionNote) {
    this.revisionNote = revisionNote;
  }

  @JsonProperty(JsonField.PATCH)
  public List<JSONPatchAction> getPatch() {
    return this.patch;
  }

  @JsonProperty(JsonField.PATCH)
  public void setPatch(List<JSONPatchAction> patch) {
    this.patch = patch;
  }
}
