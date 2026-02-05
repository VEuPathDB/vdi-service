package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "status",
    "message"
})
public class DatasetUploadStatusInfoImpl implements DatasetUploadStatusInfo {
  @JsonProperty(JsonField.STATUS)
  private DatasetUploadStatusCode status;

  @JsonProperty(JsonField.MESSAGE)
  private String message;

  @JsonProperty(JsonField.STATUS)
  public DatasetUploadStatusCode getStatus() {
    return this.status;
  }

  @JsonProperty(JsonField.STATUS)
  public void setStatus(DatasetUploadStatusCode status) {
    this.status = status;
  }

  @JsonProperty(JsonField.MESSAGE)
  public String getMessage() {
    return this.message;
  }

  @JsonProperty(JsonField.MESSAGE)
  public void setMessage(String message) {
    this.message = message;
  }
}
