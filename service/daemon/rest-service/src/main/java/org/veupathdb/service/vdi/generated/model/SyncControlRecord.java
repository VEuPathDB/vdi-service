package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;

@JsonDeserialize(
    as = SyncControlRecordImpl.class
)
public interface SyncControlRecord {
  @JsonProperty("sharesUpdateTime")
  OffsetDateTime getSharesUpdateTime();

  @JsonProperty("sharesUpdateTime")
  void setSharesUpdateTime(OffsetDateTime sharesUpdateTime);

  @JsonProperty("dataUpdateTime")
  OffsetDateTime getDataUpdateTime();

  @JsonProperty("dataUpdateTime")
  void setDataUpdateTime(OffsetDateTime dataUpdateTime);

  @JsonProperty("metaUpdateTime")
  OffsetDateTime getMetaUpdateTime();

  @JsonProperty("metaUpdateTime")
  void setMetaUpdateTime(OffsetDateTime metaUpdateTime);
}
