package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetShareReceiptImpl.class
)
public interface DatasetShareReceipt {
  @JsonProperty(JsonField.ACTION)
  ShareReceiptAction getAction();

  @JsonProperty(JsonField.ACTION)
  void setAction(ShareReceiptAction action);
}
