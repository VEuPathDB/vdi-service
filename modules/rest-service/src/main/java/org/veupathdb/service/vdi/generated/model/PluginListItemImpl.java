package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "displayName",
    "typeName",
    "typeVersion",
    "projects"
})
public class PluginListItemImpl implements PluginListItem {
  @JsonProperty("displayName")
  private String displayName;

  @JsonProperty("typeName")
  private String typeName;

  @JsonProperty("typeVersion")
  private String typeVersion;

  @JsonProperty("projects")
  private List<String> projects;

  @JsonProperty("displayName")
  public String getDisplayName() {
    return this.displayName;
  }

  @JsonProperty("displayName")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @JsonProperty("typeName")
  public String getTypeName() {
    return this.typeName;
  }

  @JsonProperty("typeName")
  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  @JsonProperty("typeVersion")
  public String getTypeVersion() {
    return this.typeVersion;
  }

  @JsonProperty("typeVersion")
  public void setTypeVersion(String typeVersion) {
    this.typeVersion = typeVersion;
  }

  @JsonProperty("projects")
  public List<String> getProjects() {
    return this.projects;
  }

  @JsonProperty("projects")
  public void setProjects(List<String> projects) {
    this.projects = projects;
  }
}
