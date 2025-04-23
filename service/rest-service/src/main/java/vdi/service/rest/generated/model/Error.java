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
    @JsonSubTypes.Type(ConflictError.class),
    @JsonSubTypes.Type(ServerError.class),
    @JsonSubTypes.Type(ForbiddenError.class),
    @JsonSubTypes.Type(UnprocessableEntityError.class),
    @JsonSubTypes.Type(NotFoundError.class),
    @JsonSubTypes.Type(UnauthorizedError.class),
    @JsonSubTypes.Type(MethodNotAllowedError.class),
    @JsonSubTypes.Type(BadRequestError.class),
    @JsonSubTypes.Type(FailedDependencyError.class),
    @JsonSubTypes.Type(java.lang.String.class),
    @JsonSubTypes.Type(Error.class)
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
