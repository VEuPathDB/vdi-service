package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = BrokenImportListingMetaImpl.class
)
public interface BrokenImportListingMeta {
  @JsonProperty(JsonField.COUNT)
  int getCount();

  @JsonProperty(JsonField.COUNT)
  void setCount(int count);

  @JsonProperty(JsonField.BEFORE)
  String getBefore();

  @JsonProperty(JsonField.BEFORE)
  void setBefore(String before);

  @JsonProperty(JsonField.AFTER)
  String getAfter();

  @JsonProperty(JsonField.AFTER)
  void setAfter(String after);

  @JsonProperty(JsonField.USER)
  long getUser();

  @JsonProperty(JsonField.USER)
  void setUser(long user);

  @JsonProperty(JsonField.LIMIT)
  int getLimit();

  @JsonProperty(JsonField.LIMIT)
  void setLimit(int limit);

  @JsonProperty(JsonField.OFFSET)
  int getOffset();

  @JsonProperty(JsonField.OFFSET)
  void setOffset(int offset);
}
