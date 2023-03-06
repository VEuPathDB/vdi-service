package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetType",
    "name",
    "summary",
    "description",
    "projects"
})
public class DatasetPostMetaImpl implements DatasetPostMeta {
  @JsonProperty("datasetType")
  private DatasetPostType datasetType;

  @JsonProperty("name")
  private String name;

  @JsonProperty("summary")
  private String summary;

  @JsonProperty("description")
  private String description;

  @JsonProperty("projects")
  private List<String> projects;

  @JsonProperty("datasetType")
  public DatasetPostType getDatasetType() {
    return this.datasetType;
  }

  @JsonProperty("datasetType")
  public void setDatasetType(DatasetPostType datasetType) {
    this.datasetType = datasetType;
  }

  @JsonProperty("name")
  public String getName() {
    return this.name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("summary")
  public String getSummary() {
    return this.summary;
  }

  @JsonProperty("summary")
  public void setSummary(String summary) {
    this.summary = summary;
  }

  @JsonProperty("description")
  public String getDescription() {
    return this.description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
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
