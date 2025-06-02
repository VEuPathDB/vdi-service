package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("server-error")
@JsonPropertyOrder({
    "status",
    "message",
    "requestId"
})
public class ServerErrorImpl implements ServerError {
  @JsonProperty(JsonField.STATUS)
  private final ErrorType status = _DISCRIMINATOR_TYPE_NAME;

  @JsonProperty(JsonField.MESSAGE)
  private String message;

  @JsonProperty(JsonField.REQUEST_ID)
  private String requestId;

  @JsonProperty(JsonField.STATUS)
  public ErrorType getStatus() {
    return this.status;
  }

  @JsonProperty(JsonField.MESSAGE)
  public String getMessage() {
    return this.message;
  }

  @JsonProperty(JsonField.MESSAGE)
  public void setMessage(String message) {
    this.message = message;
  }

  @JsonProperty(JsonField.REQUEST_ID)
  public String getRequestId() {
    return this.requestId;
  }

  @JsonProperty(JsonField.REQUEST_ID)
  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }
}
