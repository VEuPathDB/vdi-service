package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;
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
  Long getOwner();

  @JsonProperty(JsonField.OWNER)
  void setOwner(Long owner);

  @JsonProperty(JsonField.IS_DELETED)
  Boolean getIsDeleted();

  @JsonProperty(JsonField.IS_DELETED)
  void setIsDeleted(Boolean isDeleted);

  @JsonProperty(JsonField.ORIGIN)
  String getOrigin();

  @JsonProperty(JsonField.ORIGIN)
  void setOrigin(String origin);

  @JsonProperty(JsonField.CREATED)
  OffsetDateTime getCreated();

  @JsonProperty(JsonField.CREATED)
  void setCreated(OffsetDateTime created);

  @JsonProperty(JsonField.INSERTED)
  OffsetDateTime getInserted();

  @JsonProperty(JsonField.INSERTED)
  void setInserted(OffsetDateTime inserted);

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
