package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = BrokenImportListingMetaImpl.class
)
public interface BrokenImportListingMeta {
  @JsonProperty(JsonField.COUNT)
  Integer getCount();

  @JsonProperty(JsonField.COUNT)
  void setCount(Integer count);

  @JsonProperty(JsonField.BEFORE)
  String getBefore();

  @JsonProperty(JsonField.BEFORE)
  void setBefore(String before);

  @JsonProperty(JsonField.AFTER)
  String getAfter();

  @JsonProperty(JsonField.AFTER)
  void setAfter(String after);

  @JsonProperty(JsonField.USER)
  Long getUser();

  @JsonProperty(JsonField.USER)
  void setUser(Long user);

  @JsonProperty(JsonField.LIMIT)
  Integer getLimit();

  @JsonProperty(JsonField.LIMIT)
  void setLimit(Integer limit);

  @JsonProperty(JsonField.OFFSET)
  Integer getOffset();

  @JsonProperty(JsonField.OFFSET)
  void setOffset(Integer offset);
}
