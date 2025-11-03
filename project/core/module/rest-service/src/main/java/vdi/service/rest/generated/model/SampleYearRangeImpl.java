package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "start",
    "end"
})
public class SampleYearRangeImpl implements SampleYearRange {
  @JsonProperty(JsonField.START)
  private Short start;

  @JsonProperty(JsonField.END)
  private Short end;

  @JsonProperty(JsonField.START)
  public Short getStart() {
    return this.start;
  }

  @JsonProperty(JsonField.START)
  public void setStart(Short start) {
    this.start = start;
  }

  @JsonProperty(JsonField.END)
  public Short getEnd() {
    return this.end;
  }

  @JsonProperty(JsonField.END)
  public void setEnd(Short end) {
    this.end = end;
  }
}
