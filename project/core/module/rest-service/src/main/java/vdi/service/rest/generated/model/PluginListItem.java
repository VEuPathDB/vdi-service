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

  @JsonProperty(JsonField.TYPE_NAME)
  String getTypeName();

  @JsonProperty(JsonField.TYPE_NAME)
  void setTypeName(String typeName);

  @JsonProperty(JsonField.TYPE_DISPLAY_NAME)
  String getTypeDisplayName();

  @JsonProperty(JsonField.TYPE_DISPLAY_NAME)
  void setTypeDisplayName(String typeDisplayName);

  @JsonProperty(JsonField.TYPE_VERSION)
  String getTypeVersion();

  @JsonProperty(JsonField.TYPE_VERSION)
  void setTypeVersion(String typeVersion);

  @JsonProperty(JsonField.INSTALL_TARGETS)
  List<String> getInstallTargets();

  @JsonProperty(JsonField.INSTALL_TARGETS)
  void setInstallTargets(List<String> installTargets);
}
