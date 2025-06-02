package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "details",
    "ids"
})
public class BrokenDatasetInstallReportBodyImpl implements BrokenDatasetInstallReportBody {
  @JsonProperty(JsonField.DETAILS)
  private List<BrokenDatasetInstallDetails> details;

  @JsonProperty(JsonField.IDS)
  private List<String> ids;

  @JsonProperty(JsonField.DETAILS)
  public List<BrokenDatasetInstallDetails> getDetails() {
    return this.details;
  }

  @JsonProperty(JsonField.DETAILS)
  public void setDetails(List<BrokenDatasetInstallDetails> details) {
    this.details = details;
  }

  @JsonProperty(JsonField.IDS)
  public List<String> getIds() {
    return this.ids;
  }

  @JsonProperty(JsonField.IDS)
  public void setIds(List<String> ids) {
    this.ids = ids;
  }
}
