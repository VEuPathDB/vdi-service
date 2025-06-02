package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = InstallCleanupTargetImpl.class
)
public interface InstallCleanupTarget {
  @JsonProperty(JsonField.DATASET_ID)
  String getDatasetId();

  @JsonProperty(JsonField.DATASET_ID)
  void setDatasetId(String datasetId);

  @JsonProperty(JsonField.INSTALL_TARGET)
  String getInstallTarget();

  @JsonProperty(JsonField.INSTALL_TARGET)
  void setInstallTarget(String installTarget);
}
