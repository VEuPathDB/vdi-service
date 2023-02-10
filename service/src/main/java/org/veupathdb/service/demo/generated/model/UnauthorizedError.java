package org.veupathdb.service.demo.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeName("unauthorized")
@JsonDeserialize(
    as = UnauthorizedErrorImpl.class
)
public interface UnauthorizedError extends Error {
  ErrorType _DISCRIMINATOR_TYPE_NAME = ErrorType.UNAUTHORIZED;

  @JsonProperty("status")
  ErrorType getStatus();

  @JsonProperty("message")
  String getMessage();

  @JsonProperty("message")
  void setMessage(String message);
}
