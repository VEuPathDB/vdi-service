package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetInstallStatusEntryImpl.class
)
public interface DatasetInstallStatusEntry {
  @JsonProperty(JsonField.INSTALL_TARGET)
  String getInstallTarget();

  @JsonProperty(JsonField.INSTALL_TARGET)
  void setInstallTarget(String installTarget);

  @JsonProperty(JsonField.META_STATUS)
  DatasetInstallStatus getMetaStatus();

  @JsonProperty(JsonField.META_STATUS)
  void setMetaStatus(DatasetInstallStatus metaStatus);

  @JsonProperty(JsonField.META_MESSAGES)
  List<String> getMetaMessages();

  @JsonProperty(JsonField.META_MESSAGES)
  void setMetaMessages(List<String> metaMessages);

  @JsonProperty(JsonField.DATA_STATUS)
  DatasetInstallStatus getDataStatus();

  @JsonProperty(JsonField.DATA_STATUS)
  void setDataStatus(DatasetInstallStatus dataStatus);

  @JsonProperty(JsonField.DATA_MESSAGES)
  List<String> getDataMessages();

  @JsonProperty(JsonField.DATA_MESSAGES)
  void setDataMessages(List<String> dataMessages);
}
