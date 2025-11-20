package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "pluginName",
    "dataTypes",
    "installTargets"
})
public class PluginListItemImpl implements PluginListItem {
  @JsonProperty(JsonField.PLUGIN_NAME)
  private String pluginName;

  @JsonProperty(JsonField.DATA_TYPES)
  private List<PluginDataType> dataTypes;

  @JsonProperty(JsonField.INSTALL_TARGETS)
  private List<String> installTargets;

  @JsonProperty(JsonField.PLUGIN_NAME)
  public String getPluginName() {
    return this.pluginName;
  }

  @JsonProperty(JsonField.PLUGIN_NAME)
  public void setPluginName(String pluginName) {
    this.pluginName = pluginName;
  }

  @JsonProperty(JsonField.DATA_TYPES)
  public List<PluginDataType> getDataTypes() {
    return this.dataTypes;
  }

  @JsonProperty(JsonField.DATA_TYPES)
  public void setDataTypes(List<PluginDataType> dataTypes) {
    this.dataTypes = dataTypes;
  }

  @JsonProperty(JsonField.INSTALL_TARGETS)
  public List<String> getInstallTargets() {
    return this.installTargets;
  }

  @JsonProperty(JsonField.INSTALL_TARGETS)
  public void setInstallTargets(List<String> installTargets) {
    this.installTargets = installTargets;
  }
}
