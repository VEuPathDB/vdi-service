package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "count",
    "offset",
    "limit",
    "total"
})
public class AllDatasetsListMetaImpl implements AllDatasetsListMeta {
  @JsonProperty(JsonField.COUNT)
  private int count;

  @JsonProperty(JsonField.OFFSET)
  private int offset;

  @JsonProperty(JsonField.LIMIT)
  private int limit;

  @JsonProperty(JsonField.TOTAL)
  private int total;

  @JsonProperty(JsonField.COUNT)
  public int getCount() {
    return this.count;
  }

  @JsonProperty(JsonField.COUNT)
  public void setCount(int count) {
    this.count = count;
  }

  @JsonProperty(JsonField.OFFSET)
  public int getOffset() {
    return this.offset;
  }

  @JsonProperty(JsonField.OFFSET)
  public void setOffset(int offset) {
    this.offset = offset;
  }

  @JsonProperty(JsonField.LIMIT)
  public int getLimit() {
    return this.limit;
  }

  @JsonProperty(JsonField.LIMIT)
  public void setLimit(int limit) {
    this.limit = limit;
  }

  @JsonProperty(JsonField.TOTAL)
  public int getTotal() {
    return this.total;
  }

  @JsonProperty(JsonField.TOTAL)
  public void setTotal(int total) {
    this.total = total;
  }
}
