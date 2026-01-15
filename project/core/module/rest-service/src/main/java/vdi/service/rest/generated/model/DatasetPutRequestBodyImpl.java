package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.File;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "details",
    "dataFile",
    "url",
    "docFile"
})
public class DatasetPutRequestBodyImpl implements DatasetPutRequestBody {
  @JsonProperty(JsonField.DETAILS)
  private DatasetPutMetadata details;

  @JsonProperty(JsonField.DATA_FILE)
  private List<File> dataFile;

  @JsonProperty(JsonField.URL)
  private String url;

  @JsonProperty(JsonField.DOC_FILE)
  private List<File> docFile;

  @JsonProperty(JsonField.DETAILS)
  public DatasetPutMetadata getDetails() {
    return this.details;
  }

  @JsonProperty(JsonField.DETAILS)
  public void setDetails(DatasetPutMetadata details) {
    this.details = details;
  }

  @JsonProperty(JsonField.DATA_FILE)
  public List<File> getDataFile() {
    return this.dataFile;
  }

  @JsonProperty(JsonField.DATA_FILE)
  public void setDataFile(List<File> dataFile) {
    this.dataFile = dataFile;
  }

  @JsonProperty(JsonField.URL)
  public String getUrl() {
    return this.url;
  }

  @JsonProperty(JsonField.URL)
  public void setUrl(String url) {
    this.url = url;
  }

  @JsonProperty(JsonField.DOC_FILE)
  public List<File> getDocFile() {
    return this.docFile;
  }

  @JsonProperty(JsonField.DOC_FILE)
  public void setDocFile(List<File> docFile) {
    this.docFile = docFile;
  }
}
