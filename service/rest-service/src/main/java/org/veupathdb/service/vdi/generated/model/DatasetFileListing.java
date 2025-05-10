package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetFileListingImpl.class
)
public interface DatasetFileListing {
  @JsonProperty("upload")
  DatasetZipDetails getUpload();

  @JsonProperty("upload")
  void setUpload(DatasetZipDetails upload);

  @JsonProperty("install")
  DatasetZipDetails getInstall();

  @JsonProperty("install")
  void setInstall(DatasetZipDetails install);
}
