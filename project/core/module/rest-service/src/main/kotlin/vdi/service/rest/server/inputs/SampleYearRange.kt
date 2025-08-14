@file:JvmName("SampleYearRangeApiExtensions")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.reqCheckInRange
import vdi.model.data.SampleYearRange
import vdi.service.rest.generated.model.SampleYearRange as APIYearRange

private const val MinYear: Short = 1500
private const val MaxYear: Short = 2500

fun APIYearRange?.cleanup() =
  // ignore empty object
  this?.takeUnless { it.start == null && it.end == null }

fun APIYearRange.validate(jPath: String, errors: ValidationErrors) {
  start.reqCheckInRange(jPath, MinYear, MaxYear, errors)
  end.reqCheckInRange(jPath, MinYear, MaxYear, errors)
}

fun APIYearRange.toInternal() = SampleYearRange(start, end)