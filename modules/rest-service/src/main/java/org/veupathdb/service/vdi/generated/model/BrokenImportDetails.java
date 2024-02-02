package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(
    as = BrokenImportDetailsImpl.class
)
public interface BrokenImportDetails {
  @JsonProperty("datasetId")
  String getDatasetId();

  @JsonProperty("datasetId")
  void setDatasetId(String datasetId);

  @JsonProperty("owner")
  Long getOwner();

  @JsonProperty("owner")
  void setOwner(Long owner);

  @JsonProperty("datasetType")
  DatasetTypeInfo getDatasetType();

  @JsonProperty("datasetType")
  void setDatasetType(DatasetTypeInfo datasetType);

  @JsonProperty("projectIds")
  List<String> getProjectIds();

  @JsonProperty("projectIds")
  void setProjectIds(List<String> projectIds);

  @JsonProperty("messages")
  List<String> getMessages();

  @JsonProperty("messages")
  void setMessages(List<String> messages);
}
