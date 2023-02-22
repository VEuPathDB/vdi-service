package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetPostMetaImpl.class
)
public interface DatasetPostMeta {
  @JsonProperty("datasetType")
  String getDatasetType();

  @JsonProperty("datasetType")
  void setDatasetType(String datasetType);

  @JsonProperty("name")
  String getName();

  @JsonProperty("name")
  void setName(String name);

  @JsonProperty("summary")
  String getSummary();

  @JsonProperty("summary")
  void setSummary(String summary);

  @JsonProperty("description")
  String getDescription();

  @JsonProperty("description")
  void setDescription(String description);

  @JsonProperty("projects")
  List<String> getProjects();

  @JsonProperty("projects")
  void setProjects(List<String> projects);
}