package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.File;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "details",
    "dataFiles",
    "url",
    "docFiles"
})
public class DatasetPutRequestBodyImpl implements DatasetPutRequestBody {
  @JsonProperty(JsonField.DETAILS)
  private DatasetPutMetadata details;

  @JsonProperty(JsonField.DATA_FILES)
  private List<File> dataFiles;

  @JsonProperty(JsonField.URL)
  private String url;

  @JsonProperty(JsonField.DOC_FILES)
  private List<File> docFiles;

  @JsonProperty(JsonField.DETAILS)
  public DatasetPutMetadata getDetails() {
    return this.details;
  }

  @JsonProperty(JsonField.DETAILS)
  public void setDetails(DatasetPutMetadata details) {
    this.details = details;
  }

  @JsonProperty(JsonField.DATA_FILES)
  public List<File> getDataFiles() {
    return this.dataFiles;
  }

  @JsonProperty(JsonField.DATA_FILES)
  public void setDataFiles(List<File> dataFiles) {
    this.dataFiles = dataFiles;
  }

  @JsonProperty(JsonField.URL)
  public String getUrl() {
    return this.url;
  }

  @JsonProperty(JsonField.URL)
  public void setUrl(String url) {
    this.url = url;
  }

  @JsonProperty(JsonField.DOC_FILES)
  public List<File> getDocFiles() {
    return this.docFiles;
  }

  @JsonProperty(JsonField.DOC_FILES)
  public void setDocFiles(List<File> docFiles) {
    this.docFiles = docFiles;
  }
}
