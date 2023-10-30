package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = BrokenImportListingMetaImpl.class
)
public interface BrokenImportListingMeta {
  @JsonProperty("count")
  Integer getCount();

  @JsonProperty("count")
  void setCount(Integer count);

  @JsonProperty("before")
  String getBefore();

  @JsonProperty("before")
  void setBefore(String before);

  @JsonProperty("after")
  String getAfter();

  @JsonProperty("after")
  void setAfter(String after);

  @JsonProperty("user")
  Long getUser();

  @JsonProperty("user")
  void setUser(Long user);

  @JsonProperty("limit")
  Integer getLimit();

  @JsonProperty("limit")
  void setLimit(Integer limit);

  @JsonProperty("offset")
  Integer getOffset();

  @JsonProperty("offset")
  void setOffset(Integer offset);
}
