package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "status",
    "messages"
})
public class DatasetImportStatusInfoImpl implements DatasetImportStatusInfo {
  @JsonProperty(JsonField.STATUS)
  private DatasetImportStatusCode status;

  @JsonProperty(JsonField.MESSAGES)
  private List<String> messages;

  @JsonProperty(JsonField.STATUS)
  public DatasetImportStatusCode getStatus() {
    return this.status;
  }

  @JsonProperty(JsonField.STATUS)
  public void setStatus(DatasetImportStatusCode status) {
    this.status = status;
  }

  @JsonProperty(JsonField.MESSAGES)
  public List<String> getMessages() {
    return this.messages;
  }

  @JsonProperty(JsonField.MESSAGES)
  public void setMessages(List<String> messages) {
    this.messages = messages;
  }
}
