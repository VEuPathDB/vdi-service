package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetInstallStatusDetailsImpl.class
)
public interface DatasetInstallStatusDetails {
  @JsonProperty(JsonField.STATUS)
  DatasetInstallStatus getStatus();

  @JsonProperty(JsonField.STATUS)
  void setStatus(DatasetInstallStatus status);

  @JsonProperty(JsonField.MESSAGES)
  List<String> getMessages();

  @JsonProperty(JsonField.MESSAGES)
  void setMessages(List<String> messages);
}
