package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = DatasetDetailsImpl.class
)
public interface DatasetDetails {
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

  @JsonProperty("sourceUrl")
  String getSourceUrl();

  @JsonProperty("sourceUrl")
  void setSourceUrl(String sourceUrl);

  @JsonProperty("origin")
  String getOrigin();

  @JsonProperty("origin")
  void setOrigin(String origin);

  @JsonProperty("projectIDs")
  List<String> getProjectIDs();

  @JsonProperty("projectIDs")
  void setProjectIDs(List<String> projectIDs);

  @JsonProperty("visibility")
  DatasetVisibility getVisibility();

  @JsonProperty("visibility")
  void setVisibility(DatasetVisibility visibility);

  @JsonProperty(
      value = "importMessages",
      defaultValue = "[\n"
              + "\n"
              + "]"
  )
  List<String> getImportMessages();

  @JsonProperty(
      value = "importMessages",
      defaultValue = "[\n"
              + "\n"
              + "]"
  )
  void setImportMessages(List<String> importMessages);

  @JsonProperty("status")
  DatasetStatusInfo getStatus();

  @JsonProperty("status")
  void setStatus(DatasetStatusInfo status);

  @JsonProperty("shares")
  List<ShareOffer> getShares();

  @JsonProperty("shares")
  void setShares(List<ShareOffer> shares);

  @JsonProperty("files")
  List<FileSummary> getFiles();

  @JsonProperty("files")
  void setFiles(List<FileSummary> files);
}
