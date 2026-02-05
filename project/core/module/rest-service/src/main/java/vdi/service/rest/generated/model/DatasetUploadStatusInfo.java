package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetUploadStatusInfoImpl.class
)
public interface DatasetUploadStatusInfo {
  @JsonProperty(JsonField.STATUS)
  DatasetUploadStatusCode getStatus();

  @JsonProperty(JsonField.STATUS)
  void setStatus(DatasetUploadStatusCode status);

  @JsonProperty(JsonField.MESSAGE)
  String getMessage();

  @JsonProperty(JsonField.MESSAGE)
  void setMessage(String message);
}
