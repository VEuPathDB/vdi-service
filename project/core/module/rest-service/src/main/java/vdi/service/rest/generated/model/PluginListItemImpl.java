package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "pluginName",
    "type",
    "maxFileSize",
    "allowedFileExtensions",
    "installTargets"
})
public class PluginListItemImpl implements PluginListItem {
  @JsonProperty(JsonField.PLUGIN_NAME)
  private String pluginName;

  @JsonProperty(JsonField.TYPE)
  private DatasetTypeOutput type;

  @JsonProperty(JsonField.MAX_FILE_SIZE)
  private Long maxFileSize;

  @JsonProperty(JsonField.ALLOWED_FILE_EXTENSIONS)
  private List<String> allowedFileExtensions;

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

  @JsonProperty(JsonField.TYPE)
  public DatasetTypeOutput getType() {
    return this.type;
  }

  @JsonProperty(JsonField.TYPE)
  public void setType(DatasetTypeOutput type) {
    this.type = type;
  }

  @JsonProperty(JsonField.MAX_FILE_SIZE)
  public Long getMaxFileSize() {
    return this.maxFileSize;
  }

  @JsonProperty(JsonField.MAX_FILE_SIZE)
  public void setMaxFileSize(Long maxFileSize) {
    this.maxFileSize = maxFileSize;
  }

  @JsonProperty(JsonField.ALLOWED_FILE_EXTENSIONS)
  public List<String> getAllowedFileExtensions() {
    return this.allowedFileExtensions;
  }

  @JsonProperty(JsonField.ALLOWED_FILE_EXTENSIONS)
  public void setAllowedFileExtensions(List<String> allowedFileExtensions) {
    this.allowedFileExtensions = allowedFileExtensions;
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
