package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetId",
    "installTarget"
})
public class InstallCleanupTargetImpl implements InstallCleanupTarget {
  @JsonProperty(JsonField.DATASET_ID)
  private String datasetId;

  @JsonProperty(JsonField.INSTALL_TARGET)
  private String installTarget;

  @JsonProperty(JsonField.DATASET_ID)
  public String getDatasetId() {
    return this.datasetId;
  }

  @JsonProperty(JsonField.DATASET_ID)
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  @JsonProperty(JsonField.INSTALL_TARGET)
  public String getInstallTarget() {
    return this.installTarget;
  }

  @JsonProperty(JsonField.INSTALL_TARGET)
  public void setInstallTarget(String installTarget) {
    this.installTarget = installTarget;
  }
}
