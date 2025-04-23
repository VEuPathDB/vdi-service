package vdi.service.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "status"
)
@JsonSubTypes({
    @JsonSubTypes.Type(vdi.service.generated.model.ConflictError.class),
    @JsonSubTypes.Type(vdi.service.generated.model.ServerError.class),
    @JsonSubTypes.Type(vdi.service.generated.model.ForbiddenError.class),
    @JsonSubTypes.Type(vdi.service.generated.model.UnprocessableEntityError.class),
    @JsonSubTypes.Type(vdi.service.generated.model.NotFoundError.class),
    @JsonSubTypes.Type(vdi.service.generated.model.UnauthorizedError.class),
    @JsonSubTypes.Type(vdi.service.generated.model.MethodNotAllowedError.class),
    @JsonSubTypes.Type(vdi.service.generated.model.BadRequestError.class),
    @JsonSubTypes.Type(vdi.service.generated.model.FailedDependencyError.class),
    @JsonSubTypes.Type(java.lang.String.class),
    @JsonSubTypes.Type(vdi.service.generated.model.Error.class)
})
@JsonDeserialize(
    as = ErrorImpl.class
)
public interface Error {
  ErrorType _DISCRIMINATOR_TYPE_NAME = null;

  @JsonProperty(JsonField.STATUS)
  ErrorType getStatus();

  @JsonProperty(JsonField.MESSAGE)
  String getMessage();

  @JsonProperty(JsonField.MESSAGE)
  void setMessage(String message);
}
