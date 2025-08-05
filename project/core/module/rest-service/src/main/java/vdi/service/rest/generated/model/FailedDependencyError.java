package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeName("failed-dependency")
@JsonDeserialize(
    as = FailedDependencyErrorImpl.class
)
public interface FailedDependencyError extends Error {
  String _DISCRIMINATOR_TYPE_NAME = "failed-dependency";

  @JsonProperty(JsonField.STATUS)
  ErrorType getStatus();

  @JsonProperty(JsonField.MESSAGE)
  String getMessage();

  @JsonProperty(JsonField.MESSAGE)
  void setMessage(String message);

  @JsonProperty(JsonField.DEPENDENCY)
  String getDependency();

  @JsonProperty(JsonField.DEPENDENCY)
  void setDependency(String dependency);
}
