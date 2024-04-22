package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = ShareOfferImpl.class
)
public interface ShareOffer {
  @JsonProperty("recipient")
  ShareOfferRecipient getRecipient();

  @JsonProperty("recipient")
  void setRecipient(ShareOfferRecipient recipient);

  @JsonProperty("status")
  ShareOfferAction getStatus();

  @JsonProperty("status")
  void setStatus(ShareOfferAction status);
}
