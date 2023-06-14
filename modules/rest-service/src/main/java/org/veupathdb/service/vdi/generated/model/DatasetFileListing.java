package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetFileListingImpl.class
)
public interface DatasetFileListing {
  @JsonProperty("uploadFiles")
  List<DatasetFileDetails> getUploadFiles();

  @JsonProperty("uploadFiles")
  void setUploadFiles(List<DatasetFileDetails> uploadFiles);

  @JsonProperty("dataFiles")
  List<DatasetFileDetails> getDataFiles();

  @JsonProperty("dataFiles")
  void setDataFiles(List<DatasetFileDetails> dataFiles);
}
