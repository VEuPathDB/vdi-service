package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = PluginListItemImpl.class
)
public interface PluginListItem {
  @JsonProperty("displayName")
  String getDisplayName();

  @JsonProperty("displayName")
  void setDisplayName(String displayName);

  @JsonProperty("typeName")
  String getTypeName();

  @JsonProperty("typeName")
  void setTypeName(String typeName);

  @JsonProperty("typeVersion")
  String getTypeVersion();

  @JsonProperty("typeVersion")
  void setTypeVersion(String typeVersion);

  @JsonProperty("projects")
  List<String> getProjects();

  @JsonProperty("projects")
  void setProjects(List<String> projects);
}
