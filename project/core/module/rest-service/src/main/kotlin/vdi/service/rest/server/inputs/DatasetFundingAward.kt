@file:JvmName("DatasetFundingAwardInputAdaptor")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.reqCheckLength
import org.veupathdb.lib.request.validation.require
import vdi.model.meta.DatasetFundingAward
import vdi.service.rest.generated.model.DatasetFundingAward as APIAwardReference

private val AgencyNameLengthRange = 3..256
private val AwardNumberLengthRange = 3..64

fun APIAwardReference?.cleanup() = this?.apply {
  cleanupString(::getAgency)
  cleanupString(::getAwardNumber)
}

fun APIAwardReference.validate(jPath: String, index: Int, errors: ValidationErrors) {
  agency.reqCheckLength(jPath, index, AgencyNameLengthRange, errors)
  awardNumber.reqCheckLength(jPath, index, AwardNumberLengthRange, errors)
}

fun APIAwardReference.toInternal() =
  DatasetFundingAward(agency, awardNumber)

fun List<APIAwardReference>.validate(jPath: String, errors: ValidationErrors) =
  forEachIndexed { i, award -> award.require(jPath, i, errors) { validate(jPath, i, errors) } }

fun Iterable<APIAwardReference>.toInternal() = map(APIAwardReference::toInternal)