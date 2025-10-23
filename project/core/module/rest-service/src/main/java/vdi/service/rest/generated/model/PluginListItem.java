package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = PluginListItemImpl.class
)
public interface PluginListItem {
  @JsonProperty(JsonField.PLUGIN_NAME)
  String getPluginName();

  @JsonProperty(JsonField.PLUGIN_NAME)
  void setPluginName(String pluginName);

  @JsonProperty(JsonField.TYPE)
  DatasetTypeOutput getType();

  @JsonProperty(JsonField.TYPE)
  void setType(DatasetTypeOutput type);

  @JsonProperty(JsonField.MAX_FILE_SIZE)
  Long getMaxFileSize();

  @JsonProperty(JsonField.MAX_FILE_SIZE)
  void setMaxFileSize(Long maxFileSize);

  @JsonProperty(JsonField.ALLOWED_FILE_EXTENSIONS)
  List<String> getAllowedFileExtensions();

  @JsonProperty(JsonField.ALLOWED_FILE_EXTENSIONS)
  void setAllowedFileExtensions(List<String> allowedFileExtensions);

  @JsonProperty(JsonField.INSTALL_TARGETS)
  List<String> getInstallTargets();

  @JsonProperty(JsonField.INSTALL_TARGETS)
  void setInstallTargets(List<String> installTargets);
}
