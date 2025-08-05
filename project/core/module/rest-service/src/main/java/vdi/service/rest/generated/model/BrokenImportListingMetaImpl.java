package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
  @JsonProperty(JsonField.COUNT)
  private int count;

  @JsonProperty(JsonField.BEFORE)
  private String before;

  @JsonProperty(JsonField.AFTER)
  private String after;

  @JsonProperty(JsonField.USER)
  private long user;

  @JsonProperty(JsonField.LIMIT)
  private int limit;

  @JsonProperty(JsonField.OFFSET)
  private int offset;

  @JsonProperty(JsonField.COUNT)
  public int getCount() {
    return this.count;
  }

  @JsonProperty(JsonField.COUNT)
  public void setCount(int count) {
    this.count = count;
  }

  @JsonProperty(JsonField.BEFORE)
  public String getBefore() {
    return this.before;
  }

  @JsonProperty(JsonField.BEFORE)
  public void setBefore(String before) {
    this.before = before;
  }

  @JsonProperty(JsonField.AFTER)
  public String getAfter() {
    return this.after;
  }

  @JsonProperty(JsonField.AFTER)
  public void setAfter(String after) {
    this.after = after;
  }

  @JsonProperty(JsonField.USER)
  public long getUser() {
    return this.user;
  }

  @JsonProperty(JsonField.USER)
  public void setUser(long user) {
    this.user = user;
  }

  @JsonProperty(JsonField.LIMIT)
  public int getLimit() {
    return this.limit;
  }

  @JsonProperty(JsonField.LIMIT)
  public void setLimit(int limit) {
    this.limit = limit;
  }

  @JsonProperty(JsonField.OFFSET)
  public int getOffset() {
    return this.offset;
  }

  @JsonProperty(JsonField.OFFSET)
  public void setOffset(int offset) {
    this.offset = offset;
  }
}
