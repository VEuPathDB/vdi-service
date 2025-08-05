package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sharesUpdateTime",
    "dataUpdateTime",
    "metaUpdateTime"
})
public class SyncControlRecordImpl implements SyncControlRecord {
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
  )
  @JsonDeserialize(
      using = TimestampDeserializer.class
  )
  @JsonProperty(JsonField.SHARES_UPDATE_TIME)
  private Date sharesUpdateTime;

  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
  )
  @JsonDeserialize(
      using = TimestampDeserializer.class
  )
  @JsonProperty(JsonField.DATA_UPDATE_TIME)
  private Date dataUpdateTime;

  @JsonProperty(JsonField.META_UPDATE_TIME)
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
  )
  @JsonDeserialize(
      using = TimestampDeserializer.class
  )
  private Date metaUpdateTime;

  @JsonProperty(JsonField.SHARES_UPDATE_TIME)
  public Date getSharesUpdateTime() {
    return this.sharesUpdateTime;
  }

  @JsonProperty(JsonField.SHARES_UPDATE_TIME)
  public void setSharesUpdateTime(Date sharesUpdateTime) {
    this.sharesUpdateTime = sharesUpdateTime;
  }

  @JsonProperty(JsonField.DATA_UPDATE_TIME)
  public Date getDataUpdateTime() {
    return this.dataUpdateTime;
  }

  @JsonProperty(JsonField.DATA_UPDATE_TIME)
  public void setDataUpdateTime(Date dataUpdateTime) {
    this.dataUpdateTime = dataUpdateTime;
  }

  @JsonProperty(JsonField.META_UPDATE_TIME)
  public Date getMetaUpdateTime() {
    return this.metaUpdateTime;
  }

  @JsonProperty(JsonField.META_UPDATE_TIME)
  public void setMetaUpdateTime(Date metaUpdateTime) {
    this.metaUpdateTime = metaUpdateTime;
  }
}
