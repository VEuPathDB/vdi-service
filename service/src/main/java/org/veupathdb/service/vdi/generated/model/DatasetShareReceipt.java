package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = DatasetShareReceiptImpl.class
)
public interface DatasetShareReceipt {
  @JsonProperty("action")
  ShareReceiptAction getAction();

  @JsonProperty("action")
  void setAction(ShareReceiptAction action);
}
