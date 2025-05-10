package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetShareOfferImpl.class
)
public interface DatasetShareOffer {
  @JsonProperty("action")
  ShareOfferAction getAction();

  @JsonProperty("action")
  void setAction(ShareOfferAction action);
}
