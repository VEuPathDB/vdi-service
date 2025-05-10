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
  private Integer count;

  @JsonProperty(JsonField.OFFSET)
  private Integer offset;

  @JsonProperty(JsonField.LIMIT)
  private Integer limit;

  @JsonProperty(JsonField.TOTAL)
  private Integer total;

  @JsonProperty(JsonField.COUNT)
  public Integer getCount() {
    return this.count;
  }

  @JsonProperty(JsonField.COUNT)
  public void setCount(Integer count) {
    this.count = count;
  }

  @JsonProperty(JsonField.OFFSET)
  public Integer getOffset() {
    return this.offset;
  }

  @JsonProperty(JsonField.OFFSET)
  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  @JsonProperty(JsonField.LIMIT)
  public Integer getLimit() {
    return this.limit;
  }

  @JsonProperty(JsonField.LIMIT)
  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  @JsonProperty(JsonField.TOTAL)
  public Integer getTotal() {
    return this.total;
  }

  @JsonProperty(JsonField.TOTAL)
  public void setTotal(Integer total) {
    this.total = total;
  }
}
