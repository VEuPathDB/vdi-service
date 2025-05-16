package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = PluginListItemImpl.class
)
public interface PluginListItem {
  @JsonProperty(JsonField.DISPLAY_NAME)
  String getDisplayName();

  @JsonProperty(JsonField.DISPLAY_NAME)
  void setDisplayName(String displayName);

  @JsonProperty(JsonField.TYPE_NAME)
  String getTypeName();

  @JsonProperty(JsonField.TYPE_NAME)
  void setTypeName(String typeName);

  @JsonProperty(JsonField.TYPE_VERSION)
  String getTypeVersion();

  @JsonProperty(JsonField.TYPE_VERSION)
  void setTypeVersion(String typeVersion);

  @JsonProperty(JsonField.PROJECTS)
  List<String> getProjects();

  @JsonProperty(JsonField.PROJECTS)
  void setProjects(List<String> projects);
}
