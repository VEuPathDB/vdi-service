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
  private short start;

  @JsonProperty(JsonField.END)
  private short end;

  @JsonProperty(JsonField.START)
  public short getStart() {
    return this.start;
  }

  @JsonProperty(JsonField.START)
  public void setStart(short start) {
    this.start = start;
  }

  @JsonProperty(JsonField.END)
  public short getEnd() {
    return this.end;
  }

  @JsonProperty(JsonField.END)
  public void setEnd(short end) {
    this.end = end;
  }
}
