package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("action")
public class DatasetShareOfferImpl implements DatasetShareOffer {
  @JsonProperty(JsonField.ACTION)
  private ShareOfferAction action;

  @JsonProperty(JsonField.ACTION)
  public ShareOfferAction getAction() {
    return this.action;
  }

  @JsonProperty(JsonField.ACTION)
  public void setAction(ShareOfferAction action) {
    this.action = action;
  }
}
