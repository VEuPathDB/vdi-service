package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("action")
public class DatasetShareOfferImpl implements DatasetShareOffer {
  @JsonProperty("action")
  private ShareOfferAction action;

  @JsonProperty("action")
  public ShareOfferAction getAction() {
    return this.action;
  }

  @JsonProperty("action")
  public void setAction(ShareOfferAction action) {
    this.action = action;
  }
}
