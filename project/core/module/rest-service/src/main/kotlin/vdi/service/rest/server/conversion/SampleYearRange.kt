package vdi.service.rest.server.conversion

import vdi.service.rest.generated.model.SampleYearRangeImpl
import vdi.model.meta.SampleYearRange as InternalYearRange
import vdi.service.rest.generated.model.SampleYearRange as RamlYearRange

internal fun SampleYearRange(range: InternalYearRange): RamlYearRange =
  SampleYearRangeImpl().apply {
    start = range.start
    end = range.end
  }