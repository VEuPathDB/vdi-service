package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.checkInRange
import org.veupathdb.lib.request.validation.reqCheckInRange
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.require
import vdi.model.meta.SampleYearRange
import vdi.service.rest.generated.model.JsonField
import vdi.service.rest.generated.model.SampleYearRange as APIYearRange

private const val MinYear: Short = 1500
private const val MaxYear: Short = 2500

fun APIYearRange?.cleanup() =
  // ignore empty object
  this?.takeUnless { it.start == null && it.end == null }

fun APIYearRange.validate(jPath: String, errors: ValidationErrors) {
  start.reqCheckInRange(jPath..JsonField.START, MinYear, MaxYear, errors)

  val endPath = jPath..JsonField.END
  end.require(endPath, errors) {
    checkInRange(endPath, MinYear, MaxYear, errors)

    if (end < (start ?: Short.MIN_VALUE)) {
      errors.add(endPath, "end year must be greater than or equal to start year")
    }
  }
  end.reqCheckInRange(jPath..JsonField.END, MinYear, MaxYear, errors)
}

fun APIYearRange.toInternal() = SampleYearRange(start, end)