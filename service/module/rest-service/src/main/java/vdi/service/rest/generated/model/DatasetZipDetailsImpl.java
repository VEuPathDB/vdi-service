package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "zipSize",
    "contents"
})
public class DatasetZipDetailsImpl implements DatasetZipDetails {
  @JsonProperty(JsonField.ZIP_SIZE)
  private Long zipSize;

  @JsonProperty(JsonField.CONTENTS)
  private List<DatasetFileDetails> contents;

  @JsonProperty(JsonField.ZIP_SIZE)
  public Long getZipSize() {
    return this.zipSize;
  }

  @JsonProperty(JsonField.ZIP_SIZE)
  public void setZipSize(Long zipSize) {
    this.zipSize = zipSize;
  }

  @JsonProperty(JsonField.CONTENTS)
  public List<DatasetFileDetails> getContents() {
    return this.contents;
  }

  @JsonProperty(JsonField.CONTENTS)
  public void setContents(List<DatasetFileDetails> contents) {
    this.contents = contents;
  }
}
