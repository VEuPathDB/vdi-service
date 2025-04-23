package vdi.service.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = BrokenDatasetInstallDetailsImpl.class
)
public interface BrokenDatasetInstallDetails {
  @JsonProperty(JsonField.DATASET_ID)
  String getDatasetId();

  @JsonProperty(JsonField.DATASET_ID)
  void setDatasetId(String datasetId);

  @JsonProperty(JsonField.OWNER)
  Long getOwner();

  @JsonProperty(JsonField.OWNER)
  void setOwner(Long owner);

  @JsonProperty(JsonField.DATASET_TYPE)
  DatasetTypeResponseBody getDatasetType();

  @JsonProperty(JsonField.DATASET_TYPE)
  void setDatasetType(DatasetTypeResponseBody datasetType);

  @JsonProperty(JsonField.PROJECT_IDS)
  List<String> getProjectIds();

  @JsonProperty(JsonField.PROJECT_IDS)
  void setProjectIds(List<String> projectIds);

  @JsonProperty(JsonField.STATUS)
  DatasetStatusInfo getStatus();

  @JsonProperty(JsonField.STATUS)
  void setStatus(DatasetStatusInfo status);
}
