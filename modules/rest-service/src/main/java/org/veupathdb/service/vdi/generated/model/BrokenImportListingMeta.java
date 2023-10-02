package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;

@JsonDeserialize(
    as = BrokenImportListingMetaImpl.class
)
public interface BrokenImportListingMeta {
  @JsonProperty("count")
  Integer getCount();

  @JsonProperty("count")
  void setCount(Integer count);

  @JsonProperty("before")
  Date getBefore();

  @JsonProperty("before")
  void setBefore(Date before);

  @JsonProperty("after")
  Date getAfter();

  @JsonProperty("after")
  void setAfter(Date after);

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
