package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = ShareOfferImpl.class
)
public interface ShareOffer {
  @JsonProperty(JsonField.RECIPIENT)
  ShareOfferRecipient getRecipient();

  @JsonProperty(JsonField.RECIPIENT)
  void setRecipient(ShareOfferRecipient recipient);

  @JsonProperty(JsonField.STATUS)
  ShareOfferAction getStatus();

  @JsonProperty(JsonField.STATUS)
  void setStatus(ShareOfferAction status);
}
