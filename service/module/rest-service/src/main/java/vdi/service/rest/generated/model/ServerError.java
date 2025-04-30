package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeName("server-error")
@JsonDeserialize(
    as = ServerErrorImpl.class
)
public interface ServerError extends Error {
  ErrorType _DISCRIMINATOR_TYPE_NAME = ErrorType.SERVERERROR;

  @JsonProperty(JsonField.STATUS)
  ErrorType getStatus();

  @JsonProperty(JsonField.MESSAGE)
  String getMessage();

  @JsonProperty(JsonField.MESSAGE)
  void setMessage(String message);

  @JsonProperty(JsonField.REQUEST_ID)
  String getRequestId();

  @JsonProperty(JsonField.REQUEST_ID)
  void setRequestId(String requestId);
}
