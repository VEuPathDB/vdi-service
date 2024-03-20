package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeName("conflict")
@JsonDeserialize(
    as = ConflictErrorImpl.class
)
public interface ConflictError extends Error {
  ErrorType _DISCRIMINATOR_TYPE_NAME = ErrorType.CONFLICT;

  @JsonProperty("status")
  ErrorType getStatus();

  @JsonProperty("message")
  String getMessage();

  @JsonProperty("message")
  void setMessage(String message);
}
