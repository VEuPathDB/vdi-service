package vdi.service.rest.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = SampleYearRangeImpl.class
)
public interface SampleYearRange {
  @JsonProperty(JsonField.START)
  short getStart();

  @JsonProperty(JsonField.START)
  void setStart(short start);

  @JsonProperty(JsonField.END)
  short getEnd();

  @JsonProperty(JsonField.END)
  void setEnd(short end);
}
