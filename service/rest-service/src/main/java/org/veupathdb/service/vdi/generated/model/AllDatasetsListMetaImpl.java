package org.veupathdb.service.vdi.generated.model;

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
  @JsonProperty("count")
  private Integer count;

  @JsonProperty("offset")
  private Integer offset;

  @JsonProperty("limit")
  private Integer limit;

  @JsonProperty("total")
  private Integer total;

  @JsonProperty("count")
  public Integer getCount() {
    return this.count;
  }

  @JsonProperty("count")
  public void setCount(Integer count) {
    this.count = count;
  }

  @JsonProperty("offset")
  public Integer getOffset() {
    return this.offset;
  }

  @JsonProperty("offset")
  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  @JsonProperty("limit")
  public Integer getLimit() {
    return this.limit;
  }

  @JsonProperty("limit")
  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  @JsonProperty("total")
  public Integer getTotal() {
    return this.total;
  }

  @JsonProperty("total")
  public void setTotal(Integer total) {
    this.total = total;
  }
}
