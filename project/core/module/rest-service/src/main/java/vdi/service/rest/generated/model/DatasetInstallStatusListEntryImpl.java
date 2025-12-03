package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "installTarget",
    "meta",
    "data"
})
public class DatasetInstallStatusListEntryImpl implements DatasetInstallStatusListEntry {
  @JsonProperty(JsonField.INSTALL_TARGET)
  private String installTarget;

  @JsonProperty(JsonField.META)
  private DatasetInstallStatusDetails meta;

  @JsonProperty(JsonField.DATA)
  private DatasetInstallStatusDetails data;

  @JsonProperty(JsonField.INSTALL_TARGET)
  public String getInstallTarget() {
    return this.installTarget;
  }

  @JsonProperty(JsonField.INSTALL_TARGET)
  public void setInstallTarget(String installTarget) {
    this.installTarget = installTarget;
  }

  @JsonProperty(JsonField.META)
  public DatasetInstallStatusDetails getMeta() {
    return this.meta;
  }

  @JsonProperty(JsonField.META)
  public void setMeta(DatasetInstallStatusDetails meta) {
    this.meta = meta;
  }

  @JsonProperty(JsonField.DATA)
  public DatasetInstallStatusDetails getData() {
    return this.data;
  }

  @JsonProperty(JsonField.DATA)
  public void setData(DatasetInstallStatusDetails data) {
    this.data = data;
  }
}
