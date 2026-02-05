package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetImportStatusInfoImpl.class
)
public interface DatasetImportStatusInfo {
  @JsonProperty(JsonField.STATUS)
  DatasetImportStatusCode getStatus();

  @JsonProperty(JsonField.STATUS)
  void setStatus(DatasetImportStatusCode status);

  @JsonProperty(JsonField.MESSAGES)
  List<String> getMessages();

  @JsonProperty(JsonField.MESSAGES)
  void setMessages(List<String> messages);
}
