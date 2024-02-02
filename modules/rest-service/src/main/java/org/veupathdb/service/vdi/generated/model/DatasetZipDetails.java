package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(
    as = DatasetZipDetailsImpl.class
)
public interface DatasetZipDetails {
  @JsonProperty("zipSize")
  Long getZipSize();

  @JsonProperty("zipSize")
  void setZipSize(Long zipSize);

  @JsonProperty("contents")
  List<DatasetFileDetails> getContents();

  @JsonProperty("contents")
  void setContents(List<DatasetFileDetails> contents);
}
