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
public class DatasetImportStatusDetailsImpl implements DatasetImportStatusDetails {
  @JsonProperty(JsonField.STATUS)
  private DatasetImportStatus status;

  @JsonProperty(JsonField.MESSAGES)
  private List<String> messages;

  @JsonProperty(JsonField.STATUS)
  public DatasetImportStatus getStatus() {
    return this.status;
  }

  @JsonProperty(JsonField.STATUS)
  public void setStatus(DatasetImportStatus status) {
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
