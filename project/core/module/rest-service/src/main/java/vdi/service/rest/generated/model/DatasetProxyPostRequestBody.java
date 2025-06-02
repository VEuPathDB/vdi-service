package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.File;
import java.util.List;

@JsonDeserialize(
    as = DatasetProxyPostRequestBodyImpl.class
)
public interface DatasetProxyPostRequestBody {
  @JsonProperty(JsonField.DETAILS)
  DatasetProxyPostMeta getDetails();

  @JsonProperty(JsonField.DETAILS)
  void setDetails(DatasetProxyPostMeta details);

  @JsonProperty(JsonField.DATA_FILES)
  List<File> getDataFiles();

  @JsonProperty(JsonField.DATA_FILES)
  void setDataFiles(List<File> dataFiles);

  @JsonProperty(JsonField.URL)
  String getUrl();

  @JsonProperty(JsonField.URL)
  void setUrl(String url);

  @JsonProperty(JsonField.DOC_FILES)
  List<File> getDocFiles();

  @JsonProperty(JsonField.DOC_FILES)
  void setDocFiles(List<File> docFiles);
}
