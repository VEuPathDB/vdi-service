package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = AllDatasetsListMetaImpl.class
)
public interface AllDatasetsListMeta {
  @JsonProperty("count")
  Integer getCount();

  @JsonProperty("count")
  void setCount(Integer count);

  @JsonProperty("offset")
  Integer getOffset();

  @JsonProperty("offset")
  void setOffset(Integer offset);

  @JsonProperty("limit")
  Integer getLimit();

  @JsonProperty("limit")
  void setLimit(Integer limit);

  @JsonProperty("total")
  Integer getTotal();

  @JsonProperty("total")
  void setTotal(Integer total);
}
