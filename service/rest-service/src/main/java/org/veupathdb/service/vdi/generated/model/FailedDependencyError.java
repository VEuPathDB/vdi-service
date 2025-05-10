package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeName("failed-dependency")
@JsonDeserialize(
    as = FailedDependencyErrorImpl.class
)
public interface FailedDependencyError extends Error {
  ErrorType _DISCRIMINATOR_TYPE_NAME = ErrorType.FAILEDDEPENDENCY;

  @JsonProperty("status")
  ErrorType getStatus();

  @JsonProperty("message")
  String getMessage();

  @JsonProperty("message")
  void setMessage(String message);

  @JsonProperty("dependency")
  String getDependency();

  @JsonProperty("dependency")
  void setDependency(String dependency);
}
