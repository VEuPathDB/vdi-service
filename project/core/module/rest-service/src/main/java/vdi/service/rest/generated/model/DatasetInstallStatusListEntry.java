package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetInstallStatusListEntryImpl.class
)
public interface DatasetInstallStatusListEntry {
  @JsonProperty(JsonField.INSTALL_TARGET)
  String getInstallTarget();

  @JsonProperty(JsonField.INSTALL_TARGET)
  void setInstallTarget(String installTarget);

  @JsonProperty(JsonField.META)
  DatasetInstallStatusDetails getMeta();

  @JsonProperty(JsonField.META)
  void setMeta(DatasetInstallStatusDetails meta);

  @JsonProperty(JsonField.DATA)
  DatasetInstallStatusDetails getData();

  @JsonProperty(JsonField.DATA)
  void setData(DatasetInstallStatusDetails data);
}
