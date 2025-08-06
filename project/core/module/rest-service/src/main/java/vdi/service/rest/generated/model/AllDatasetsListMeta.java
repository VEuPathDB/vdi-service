package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = AllDatasetsListMetaImpl.class
)
public interface AllDatasetsListMeta {
  @JsonProperty(JsonField.COUNT)
  Integer getCount();

  @JsonProperty(JsonField.COUNT)
  void setCount(Integer count);

  @JsonProperty(JsonField.OFFSET)
  Integer getOffset();

  @JsonProperty(JsonField.OFFSET)
  void setOffset(Integer offset);

  @JsonProperty(JsonField.LIMIT)
  Integer getLimit();

  @JsonProperty(JsonField.LIMIT)
  void setLimit(Integer limit);

  @JsonProperty(JsonField.TOTAL)
  Integer getTotal();

  @JsonProperty(JsonField.TOTAL)
  void setTotal(Integer total);
}
