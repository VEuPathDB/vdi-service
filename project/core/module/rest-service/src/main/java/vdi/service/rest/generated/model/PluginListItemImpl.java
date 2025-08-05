package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "displayName",
    "typeName",
    "typeVersion",
    "installTargets"
})
public class PluginListItemImpl implements PluginListItem {
  @JsonProperty(JsonField.DISPLAY_NAME)
  private String displayName;

  @JsonProperty(JsonField.TYPE_NAME)
  private String typeName;

  @JsonProperty(JsonField.TYPE_VERSION)
  private String typeVersion;

  @JsonProperty(JsonField.INSTALL_TARGETS)
  private List<String> installTargets;

  @JsonProperty(JsonField.DISPLAY_NAME)
  public String getDisplayName() {
    return this.displayName;
  }

  @JsonProperty(JsonField.DISPLAY_NAME)
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @JsonProperty(JsonField.TYPE_NAME)
  public String getTypeName() {
    return this.typeName;
  }

  @JsonProperty(JsonField.TYPE_NAME)
  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  @JsonProperty(JsonField.TYPE_VERSION)
  public String getTypeVersion() {
    return this.typeVersion;
  }

  @JsonProperty(JsonField.TYPE_VERSION)
  public void setTypeVersion(String typeVersion) {
    this.typeVersion = typeVersion;
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
