package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "upload",
    "install",
    "documents",
    "variableProperties"
})
public class DatasetFileListingImpl implements DatasetFileListing {
  @JsonProperty(JsonField.UPLOAD)
  private DatasetZipDetails upload;

  @JsonProperty(JsonField.INSTALL)
  private DatasetZipDetails install;

  @JsonProperty(JsonField.DOCUMENTS)
  private List<DatasetFileDetails> documents;

  @JsonProperty(JsonField.VARIABLE_PROPERTIES)
  private List<DatasetFileDetails> variableProperties;

  @JsonProperty(JsonField.UPLOAD)
  public DatasetZipDetails getUpload() {
    return this.upload;
  }

  @JsonProperty(JsonField.UPLOAD)
  public void setUpload(DatasetZipDetails upload) {
    this.upload = upload;
  }

  @JsonProperty(JsonField.INSTALL)
  public DatasetZipDetails getInstall() {
    return this.install;
  }

  @JsonProperty(JsonField.INSTALL)
  public void setInstall(DatasetZipDetails install) {
    this.install = install;
  }

  @JsonProperty(JsonField.DOCUMENTS)
  public List<DatasetFileDetails> getDocuments() {
    return this.documents;
  }

  @JsonProperty(JsonField.DOCUMENTS)
  public void setDocuments(List<DatasetFileDetails> documents) {
    this.documents = documents;
  }

  @JsonProperty(JsonField.VARIABLE_PROPERTIES)
  public List<DatasetFileDetails> getVariableProperties() {
    return this.variableProperties;
  }

  @JsonProperty(JsonField.VARIABLE_PROPERTIES)
  public void setVariableProperties(List<DatasetFileDetails> variableProperties) {
    this.variableProperties = variableProperties;
  }
}
