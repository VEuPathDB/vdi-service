package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "recipient",
    "status"
})
public class ShareOfferImpl implements ShareOffer {
  @JsonProperty("recipient")
  private ShareOfferRecipient recipient;

  @JsonProperty("status")
  private ShareOfferAction status;

  @JsonProperty("recipient")
  public ShareOfferRecipient getRecipient() {
    return this.recipient;
  }

  @JsonProperty("recipient")
  public void setRecipient(ShareOfferRecipient recipient) {
    this.recipient = recipient;
  }

  @JsonProperty("status")
  public ShareOfferAction getStatus() {
    return this.status;
  }

  @JsonProperty("status")
  public void setStatus(ShareOfferAction status) {
    this.status = status;
  }
}
