package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeName("bad-method")
@JsonDeserialize(
    as = MethodNotAllowedErrorImpl.class
)
public interface MethodNotAllowedError extends Error {
  String _DISCRIMINATOR_TYPE_NAME = "bad-method";

  @JsonProperty("status")
  ErrorType getStatus();

  @JsonProperty("message")
  String getMessage();

  @JsonProperty("message")
  void setMessage(String message);
}
