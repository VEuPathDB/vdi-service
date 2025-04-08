package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("bad-method")
@JsonPropertyOrder({
    "status",
    "message"
})
public class MethodNotAllowedErrorImpl implements MethodNotAllowedError {
  @JsonProperty(JsonField.STATUS)
  private final ErrorType status = _DISCRIMINATOR_TYPE_NAME;

  @JsonProperty(JsonField.MESSAGE)
  private String message;

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
}
