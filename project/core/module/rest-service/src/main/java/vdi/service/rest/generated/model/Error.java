package vdi.service.rest.generated.model;

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
    @JsonSubTypes.Type(vdi.service.rest.generated.model.TooEarlyError.class),
    @JsonSubTypes.Type(vdi.service.rest.generated.model.ConflictError.class),
    @JsonSubTypes.Type(vdi.service.rest.generated.model.ServerError.class),
    @JsonSubTypes.Type(vdi.service.rest.generated.model.ForbiddenError.class),
    @JsonSubTypes.Type(vdi.service.rest.generated.model.UnprocessableEntityError.class),
    @JsonSubTypes.Type(vdi.service.rest.generated.model.NotFoundError.class),
    @JsonSubTypes.Type(vdi.service.rest.generated.model.UnauthorizedError.class),
    @JsonSubTypes.Type(vdi.service.rest.generated.model.MethodNotAllowedError.class),
    @JsonSubTypes.Type(vdi.service.rest.generated.model.BadRequestError.class),
    @JsonSubTypes.Type(vdi.service.rest.generated.model.GoneError.class),
    @JsonSubTypes.Type(vdi.service.rest.generated.model.FailedDependencyError.class),
    @JsonSubTypes.Type(vdi.service.rest.generated.model.Error.class)
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
