package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("failed-dependency")
@JsonPropertyOrder({
    "status",
    "message",
    "dependency"
})
public class FailedDependencyErrorImpl implements FailedDependencyError {
  @JsonProperty("status")
  private final ErrorType status = _DISCRIMINATOR_TYPE_NAME;

  @JsonProperty("message")
  private String message;

  @JsonProperty("dependency")
  private String dependency;

  @JsonProperty("status")
  public ErrorType getStatus() {
    return this.status;
  }

  @JsonProperty("message")
  public String getMessage() {
    return this.message;
  }

  @JsonProperty("message")
  public void setMessage(String message) {
    this.message = message;
  }

  @JsonProperty("dependency")
  public String getDependency() {
    return this.dependency;
  }

  @JsonProperty("dependency")
  public void setDependency(String dependency) {
    this.dependency = dependency;
  }
}
