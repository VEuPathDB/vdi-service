package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;
import java.util.List;

@JsonDeserialize(
    as = AllDatasetsListEntryImpl.class
)
public interface AllDatasetsListEntry {
  @JsonProperty(JsonField.DATASET_ID)
  String getDatasetId();

  @JsonProperty(JsonField.DATASET_ID)
  void setDatasetId(String datasetId);

  @JsonProperty(JsonField.OWNER)
  long getOwner();

  @JsonProperty(JsonField.OWNER)
  void setOwner(long owner);

  @JsonProperty(JsonField.DATASET_TYPE)
  DatasetTypeOutput getDatasetType();

  @JsonProperty(JsonField.DATASET_TYPE)
  void setDatasetType(DatasetTypeOutput datasetType);

  @JsonProperty(JsonField.VISIBILITY)
  DatasetVisibility getVisibility();

  @JsonProperty(JsonField.VISIBILITY)
  void setVisibility(DatasetVisibility visibility);

  @JsonProperty(JsonField.NAME)
  String getName();

  @JsonProperty(JsonField.NAME)
  void setName(String name);

  @JsonProperty(JsonField.ORIGIN)
  String getOrigin();

  @JsonProperty(JsonField.ORIGIN)
  void setOrigin(String origin);

  @JsonProperty(JsonField.INSTALL_TARGETS)
  List<String> getInstallTargets();

  @JsonProperty(JsonField.INSTALL_TARGETS)
  void setInstallTargets(List<String> installTargets);

  @JsonProperty(JsonField.STATUS)
  DatasetStatusInfo getStatus();

  @JsonProperty(JsonField.STATUS)
  void setStatus(DatasetStatusInfo status);

  @JsonProperty(JsonField.CREATED)
  Date getCreated();

  @JsonProperty(JsonField.CREATED)
  void setCreated(Date created);

  @JsonProperty(JsonField.IS_DELETED)
  boolean getIsDeleted();

  @JsonProperty(JsonField.IS_DELETED)
  void setIsDeleted(boolean isDeleted);

  @JsonProperty(JsonField.SUMMARY)
  String getSummary();

  @JsonProperty(JsonField.SUMMARY)
  void setSummary(String summary);

  @JsonProperty(JsonField.DESCRIPTION)
  String getDescription();

  @JsonProperty(JsonField.DESCRIPTION)
  void setDescription(String description);

  @JsonProperty(JsonField.SOURCE_URL)
  String getSourceUrl();

  @JsonProperty(JsonField.SOURCE_URL)
  void setSourceUrl(String sourceUrl);
}
