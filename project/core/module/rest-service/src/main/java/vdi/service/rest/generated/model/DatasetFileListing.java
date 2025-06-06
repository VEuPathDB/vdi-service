package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetFileListingImpl.class
)
public interface DatasetFileListing {
  @JsonProperty(JsonField.UPLOAD)
  DatasetZipDetails getUpload();

  @JsonProperty(JsonField.UPLOAD)
  void setUpload(DatasetZipDetails upload);

  @JsonProperty(JsonField.INSTALL)
  DatasetZipDetails getInstall();

  @JsonProperty(JsonField.INSTALL)
  void setInstall(DatasetZipDetails install);

  @JsonProperty(JsonField.DOCUMENTS)
  List<DatasetFileDetails> getDocuments();

  @JsonProperty(JsonField.DOCUMENTS)
  void setDocuments(List<DatasetFileDetails> documents);
}
