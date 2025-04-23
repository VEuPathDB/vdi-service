package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetShareOfferImpl.class
)
public interface DatasetShareOffer {
  @JsonProperty(JsonField.ACTION)
  ShareOfferAction getAction();

  @JsonProperty(JsonField.ACTION)
  void setAction(ShareOfferAction action);
}
