package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("action")
public class DatasetShareReceiptImpl implements DatasetShareReceipt {
  @JsonProperty(JsonField.ACTION)
  private ShareReceiptAction action;

  @JsonProperty(JsonField.ACTION)
  public ShareReceiptAction getAction() {
    return this.action;
  }

  @JsonProperty(JsonField.ACTION)
  public void setAction(ShareReceiptAction action) {
    this.action = action;
  }
}
