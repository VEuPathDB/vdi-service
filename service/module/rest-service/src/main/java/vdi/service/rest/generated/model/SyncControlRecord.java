package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.OffsetDateTime;

@JsonDeserialize(
    as = SyncControlRecordImpl.class
)
public interface SyncControlRecord {
  @JsonProperty(JsonField.SHARES_UPDATE_TIME)
  OffsetDateTime getSharesUpdateTime();

  @JsonProperty(JsonField.SHARES_UPDATE_TIME)
  void setSharesUpdateTime(OffsetDateTime sharesUpdateTime);

  @JsonProperty(JsonField.DATA_UPDATE_TIME)
  OffsetDateTime getDataUpdateTime();

  @JsonProperty(JsonField.DATA_UPDATE_TIME)
  void setDataUpdateTime(OffsetDateTime dataUpdateTime);

  @JsonProperty(JsonField.META_UPDATE_TIME)
  OffsetDateTime getMetaUpdateTime();

  @JsonProperty(JsonField.META_UPDATE_TIME)
  void setMetaUpdateTime(OffsetDateTime metaUpdateTime);
}
