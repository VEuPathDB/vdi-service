package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "recipient",
    "status"
})
public class ShareOfferImpl implements ShareOffer {
  @JsonProperty(JsonField.RECIPIENT)
  private ShareOfferRecipient recipient;

  @JsonProperty(JsonField.STATUS)
  private ShareOfferAction status;

  @JsonProperty(JsonField.RECIPIENT)
  public ShareOfferRecipient getRecipient() {
    return this.recipient;
  }

  @JsonProperty(JsonField.RECIPIENT)
  public void setRecipient(ShareOfferRecipient recipient) {
    this.recipient = recipient;
  }

  @JsonProperty(JsonField.STATUS)
  public ShareOfferAction getStatus() {
    return this.status;
  }

  @JsonProperty(JsonField.STATUS)
  public void setStatus(ShareOfferAction status) {
    this.status = status;
  }
}
