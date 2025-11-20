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

  @JsonProperty(JsonField.DATA_TYPES)
  List<PluginDataType> getDataTypes();

  @JsonProperty(JsonField.DATA_TYPES)
  void setDataTypes(List<PluginDataType> dataTypes);

  @JsonProperty(JsonField.INSTALL_TARGETS)
  List<String> getInstallTargets();

  @JsonProperty(JsonField.INSTALL_TARGETS)
  void setInstallTargets(List<String> installTargets);
}
