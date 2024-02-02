package org.veupathdb.service.vdi.generated.model;

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
  @JsonProperty("sharesUpdateTime")
  private OffsetDateTime sharesUpdateTime;

  @JsonProperty("dataUpdateTime")
  private OffsetDateTime dataUpdateTime;

  @JsonProperty("metaUpdateTime")
  private OffsetDateTime metaUpdateTime;

  @JsonProperty("sharesUpdateTime")
  public OffsetDateTime getSharesUpdateTime() {
    return this.sharesUpdateTime;
  }

  @JsonProperty("sharesUpdateTime")
  public void setSharesUpdateTime(OffsetDateTime sharesUpdateTime) {
    this.sharesUpdateTime = sharesUpdateTime;
  }

  @JsonProperty("dataUpdateTime")
  public OffsetDateTime getDataUpdateTime() {
    return this.dataUpdateTime;
  }

  @JsonProperty("dataUpdateTime")
  public void setDataUpdateTime(OffsetDateTime dataUpdateTime) {
    this.dataUpdateTime = dataUpdateTime;
  }

  @JsonProperty("metaUpdateTime")
  public OffsetDateTime getMetaUpdateTime() {
    return this.metaUpdateTime;
  }

  @JsonProperty("metaUpdateTime")
  public void setMetaUpdateTime(OffsetDateTime metaUpdateTime) {
    this.metaUpdateTime = metaUpdateTime;
  }
}
