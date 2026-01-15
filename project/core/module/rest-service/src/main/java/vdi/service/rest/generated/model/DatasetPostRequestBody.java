package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.File;
import java.util.List;

@JsonDeserialize(
    as = DatasetPostRequestBodyImpl.class
)
public interface DatasetPostRequestBody {
  @JsonProperty(JsonField.DETAILS)
  DatasetPostMeta getDetails();

  @JsonProperty(JsonField.DETAILS)
  void setDetails(DatasetPostMeta details);

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

  @JsonProperty(JsonField.DATA_PROPERTIES_FILE)
  List<File> getDataPropertiesFile();

  @JsonProperty(JsonField.DATA_PROPERTIES_FILE)
  void setDataPropertiesFile(List<File> dataPropertiesFile);
}
