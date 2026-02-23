package vdi.service.rest.conversion;

import org.jetbrains.annotations.NotNull;
import vdi.service.rest.generated.model.SampleYearRangeImpl;

public class SampleYearRange extends SampleYearRangeImpl {
  public SampleYearRange(@NotNull vdi.model.meta.SampleYearRange range) {
    setStart(range.getStart());
    setEnd(range.getEnd());
  }
}
