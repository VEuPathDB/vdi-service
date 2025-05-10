package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sharesUpdateTime",
    "dataUpdateTime",
    "metaUpdateTime"
})
public class SyncControlRecordImpl implements SyncControlRecord {
  @JsonProperty(JsonField.SHARES_UPDATE_TIME)
  private OffsetDateTime sharesUpdateTime;

  @JsonProperty(JsonField.DATA_UPDATE_TIME)
  private OffsetDateTime dataUpdateTime;

  @JsonProperty(JsonField.META_UPDATE_TIME)
  private OffsetDateTime metaUpdateTime;

  @JsonProperty(JsonField.SHARES_UPDATE_TIME)
  public OffsetDateTime getSharesUpdateTime() {
    return this.sharesUpdateTime;
  }

  @JsonProperty(JsonField.SHARES_UPDATE_TIME)
  public void setSharesUpdateTime(OffsetDateTime sharesUpdateTime) {
    this.sharesUpdateTime = sharesUpdateTime;
  }

  @JsonProperty(JsonField.DATA_UPDATE_TIME)
  public OffsetDateTime getDataUpdateTime() {
    return this.dataUpdateTime;
  }

  @JsonProperty(JsonField.DATA_UPDATE_TIME)
  public void setDataUpdateTime(OffsetDateTime dataUpdateTime) {
    this.dataUpdateTime = dataUpdateTime;
  }

  @JsonProperty(JsonField.META_UPDATE_TIME)
  public OffsetDateTime getMetaUpdateTime() {
    return this.metaUpdateTime;
  }

  @JsonProperty(JsonField.META_UPDATE_TIME)
  public void setMetaUpdateTime(OffsetDateTime metaUpdateTime) {
    this.metaUpdateTime = metaUpdateTime;
  }
}
