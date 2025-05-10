package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeName("bad-method")
@JsonDeserialize(
    as = MethodNotAllowedErrorImpl.class
)
public interface MethodNotAllowedError extends Error {
  ErrorType _DISCRIMINATOR_TYPE_NAME = ErrorType.BADMETHOD;

  @JsonProperty(JsonField.STATUS)
  ErrorType getStatus();

  @JsonProperty(JsonField.MESSAGE)
  String getMessage();

  @JsonProperty(JsonField.MESSAGE)
  void setMessage(String message);
}
