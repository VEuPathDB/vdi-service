package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.File;
import java.util.List;

@JsonDeserialize(
    as = DatasetPutRequestBodyImpl.class
)
public interface DatasetPutRequestBody {
  @JsonProperty(JsonField.DETAILS)
  DatasetPutMetadata getDetails();

  @JsonProperty(JsonField.DETAILS)
  void setDetails(DatasetPutMetadata details);

  @JsonProperty(JsonField.DATA_FILE)
  List<File> getDataFile();

  @JsonProperty(JsonField.DATA_FILE)
  void setDataFile(List<File> dataFile);

  @JsonProperty(JsonField.URL)
  String getUrl();

  @JsonProperty(JsonField.URL)
  void setUrl(String url);

  @JsonProperty(JsonField.DOC_FILE)
  List<File> getDocFile();

  @JsonProperty(JsonField.DOC_FILE)
  void setDocFile(List<File> docFile);
}
