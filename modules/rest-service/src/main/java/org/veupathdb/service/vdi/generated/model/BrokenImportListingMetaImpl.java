package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "count",
    "before",
    "after",
    "user",
    "limit",
    "offset"
})
public class BrokenImportListingMetaImpl implements BrokenImportListingMeta {
  @JsonProperty("count")
  private Integer count;

  @JsonProperty("before")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
  )
  @JsonDeserialize(
      using = TimestampDeserializer.class
  )
  private Date before;

  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
  )
  @JsonDeserialize(
      using = TimestampDeserializer.class
  )
  @JsonProperty("after")
  private Date after;

  @JsonProperty("user")
  private Long user;

  @JsonProperty("limit")
  private Integer limit;

  @JsonProperty("offset")
  private Integer offset;

  @JsonProperty("count")
  public Integer getCount() {
    return this.count;
  }

  @JsonProperty("count")
  public void setCount(Integer count) {
    this.count = count;
  }

  @JsonProperty("before")
  public Date getBefore() {
    return this.before;
  }

  @JsonProperty("before")
  public void setBefore(Date before) {
    this.before = before;
  }

  @JsonProperty("after")
  public Date getAfter() {
    return this.after;
  }

  @JsonProperty("after")
  public void setAfter(Date after) {
    this.after = after;
  }

  @JsonProperty("user")
  public Long getUser() {
    return this.user;
  }

  @JsonProperty("user")
  public void setUser(Long user) {
    this.user = user;
  }

  @JsonProperty("limit")
  public Integer getLimit() {
    return this.limit;
  }

  @JsonProperty("limit")
  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  @JsonProperty("offset")
  public Integer getOffset() {
    return this.offset;
  }

  @JsonProperty("offset")
  public void setOffset(Integer offset) {
    this.offset = offset;
  }
}
