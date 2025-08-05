package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;
import java.util.List;

@JsonDeserialize(
    as = InternalDatasetDetailsImpl.class
)
public interface InternalDatasetDetails {
  @JsonProperty(JsonField.DATASET_TYPE)
  DatasetTypeOutput getDatasetType();

  @JsonProperty(JsonField.DATASET_TYPE)
  void setDatasetType(DatasetTypeOutput datasetType);

  @JsonProperty(JsonField.OWNER)
  long getOwner();

  @JsonProperty(JsonField.OWNER)
  void setOwner(long owner);

  @JsonProperty(JsonField.IS_DELETED)
  boolean getIsDeleted();

  @JsonProperty(JsonField.IS_DELETED)
  void setIsDeleted(boolean isDeleted);

  @JsonProperty(JsonField.ORIGIN)
  String getOrigin();

  @JsonProperty(JsonField.ORIGIN)
  void setOrigin(String origin);

  @JsonProperty(JsonField.CREATED)
  Date getCreated();

  @JsonProperty(JsonField.CREATED)
  void setCreated(Date created);

  @JsonProperty(JsonField.INSERTED)
  Date getInserted();

  @JsonProperty(JsonField.INSERTED)
  void setInserted(Date inserted);

  @JsonProperty(JsonField.NAME)
  String getName();

  @JsonProperty(JsonField.NAME)
  void setName(String name);

  @JsonProperty(JsonField.INSTALL_TARGETS)
  List<String> getInstallTargets();

  @JsonProperty(JsonField.INSTALL_TARGETS)
  void setInstallTargets(List<String> installTargets);

  @JsonProperty(JsonField.STATUS)
  String getStatus();

  @JsonProperty(JsonField.STATUS)
  void setStatus(String status);

  @JsonProperty(JsonField.IMPORT_MESSAGES)
  List<String> getImportMessages();

  @JsonProperty(JsonField.IMPORT_MESSAGES)
  void setImportMessages(List<String> importMessages);

  @JsonProperty(JsonField.VISIBILITY)
  DatasetVisibility getVisibility();

  @JsonProperty(JsonField.VISIBILITY)
  void setVisibility(DatasetVisibility visibility);

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

  @JsonProperty(JsonField.SYNC_CONTROL)
  SyncControlRecord getSyncControl();

  @JsonProperty(JsonField.SYNC_CONTROL)
  void setSyncControl(SyncControlRecord syncControl);

  @JsonProperty(JsonField.UPLOAD_FILES)
  List<String> getUploadFiles();

  @JsonProperty(JsonField.UPLOAD_FILES)
  void setUploadFiles(List<String> uploadFiles);

  @JsonProperty(JsonField.INSTALL_FILES)
  List<String> getInstallFiles();

  @JsonProperty(JsonField.INSTALL_FILES)
  void setInstallFiles(List<String> installFiles);
}
