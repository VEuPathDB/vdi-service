package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;

@JsonDeserialize(
    as = SyncControlRecordImpl.class
)
public interface SyncControlRecord {
  @JsonProperty(JsonField.SHARES_UPDATE_TIME)
  Date getSharesUpdateTime();

  @JsonProperty(JsonField.SHARES_UPDATE_TIME)
  void setSharesUpdateTime(Date sharesUpdateTime);

  @JsonProperty(JsonField.DATA_UPDATE_TIME)
  Date getDataUpdateTime();

  @JsonProperty(JsonField.DATA_UPDATE_TIME)
  void setDataUpdateTime(Date dataUpdateTime);

  @JsonProperty(JsonField.META_UPDATE_TIME)
  Date getMetaUpdateTime();

  @JsonProperty(JsonField.META_UPDATE_TIME)
  void setMetaUpdateTime(Date metaUpdateTime);
}
