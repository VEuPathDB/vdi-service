package org.veupathdb.service.vdi.generated.model;

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
  @JsonProperty("zipSize")
  private Long zipSize;

  @JsonProperty("contents")
  private List<DatasetFileDetails> contents;

  @JsonProperty("zipSize")
  public Long getZipSize() {
    return this.zipSize;
  }

  @JsonProperty("zipSize")
  public void setZipSize(Long zipSize) {
    this.zipSize = zipSize;
  }

  @JsonProperty("contents")
  public List<DatasetFileDetails> getContents() {
    return this.contents;
  }

  @JsonProperty("contents")
  public void setContents(List<DatasetFileDetails> contents) {
    this.contents = contents;
  }
}
