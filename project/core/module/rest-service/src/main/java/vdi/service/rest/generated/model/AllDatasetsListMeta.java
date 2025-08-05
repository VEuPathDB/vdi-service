package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = AllDatasetsListMetaImpl.class
)
public interface AllDatasetsListMeta {
  @JsonProperty(JsonField.COUNT)
  int getCount();

  @JsonProperty(JsonField.COUNT)
  void setCount(int count);

  @JsonProperty(JsonField.OFFSET)
  int getOffset();

  @JsonProperty(JsonField.OFFSET)
  void setOffset(int offset);

  @JsonProperty(JsonField.LIMIT)
  int getLimit();

  @JsonProperty(JsonField.LIMIT)
  void setLimit(int limit);

  @JsonProperty(JsonField.TOTAL)
  int getTotal();

  @JsonProperty(JsonField.TOTAL)
  void setTotal(int total);
}
