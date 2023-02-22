package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetListEntryImpl.class
)
public interface DatasetListEntry {
  @JsonProperty("datasetID")
  String getDatasetID();

  @JsonProperty("datasetID")
  void setDatasetID(String datasetID);

  @JsonProperty("owner")
  DatasetOwner getOwner();

  @JsonProperty("owner")
  void setOwner(DatasetOwner owner);

  @JsonProperty("datasetType")
  DatasetTypeInfo getDatasetType();

  @JsonProperty("datasetType")
  void setDatasetType(DatasetTypeInfo datasetType);

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

  @JsonProperty("projectIDs")
  List<String> getProjectIDs();

  @JsonProperty("projectIDs")
  void setProjectIDs(List<String> projectIDs);

  @JsonProperty("status")
  DatasetStatusInfo getStatus();

  @JsonProperty("status")
  void setStatus(DatasetStatusInfo status);
}