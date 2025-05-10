package org.veupathdb.service.vdi.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("action")
public class DatasetShareReceiptImpl implements DatasetShareReceipt {
  @JsonProperty("action")
  private ShareReceiptAction action;

  @JsonProperty("action")
  public ShareReceiptAction getAction() {
    return this.action;
  }

  @JsonProperty("action")
  public void setAction(ShareReceiptAction action) {
    this.action = action;
  }
}
