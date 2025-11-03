package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = SampleYearRangeImpl.class
)
public interface SampleYearRange {
  @JsonProperty(JsonField.START)
  Short getStart();

  @JsonProperty(JsonField.START)
  void setStart(Short start);

  @JsonProperty(JsonField.END)
  Short getEnd();

  @JsonProperty(JsonField.END)
  void setEnd(Short end);
}
