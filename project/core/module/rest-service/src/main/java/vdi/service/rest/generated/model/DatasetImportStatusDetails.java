package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetImportStatusDetailsImpl.class
)
public interface DatasetImportStatusDetails {
  @JsonProperty(JsonField.STATUS)
  DatasetImportStatus getStatus();

  @JsonProperty(JsonField.STATUS)
  void setStatus(DatasetImportStatus status);

  @JsonProperty(JsonField.MESSAGES)
  List<String> getMessages();

  @JsonProperty(JsonField.MESSAGES)
  void setMessages(List<String> messages);
}
