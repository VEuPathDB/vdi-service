@file:JvmName("DatasetContactInputAdaptor")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.model.meta.DatasetContact
import vdi.service.rest.generated.model.JsonField
import vdi.service.rest.generated.model.DatasetContact as APIContact

private val ContactNameLengthRange = 3..300
private val EmailLengthRange = 5..1024
private val AffiliationLengthRange = 3..4000
private val CountryLengthRange = Range3To200

internal fun APIContact?.cleanup() = this?.apply {
  cleanupString(::getFirstName)
  cleanupString(::getMiddleName)
  cleanupString(::getLastName)
  ensureNotNull(::getIsPrimary, false)
  cleanupString(::getEmail)
  cleanupString(::getAffiliation)
  cleanupString(::getCountry)
}

private fun APIContact.validate(
  jPath: String,
  index: Int,
  errors: ValidationErrors,
  strict: Boolean = false,
) {
  // conditionally require "strict" fields
  val strictValidator: String.(String, Int, IntRange, ValidationErrors) -> Unit =
    if (strict)
      String::reqCheckLength
    else
      String::checkLength

  firstName.strictValidator(jPath..JsonField.FIRST_NAME, index, ContactNameLengthRange, errors)
  middleName?.checkLength(jPath..JsonField.MIDDLE_NAME, index, ContactNameLengthRange, errors)
  lastName.strictValidator(jPath..JsonField.LAST_NAME, index, ContactNameLengthRange, errors)
  email.strictValidator(jPath..JsonField.EMAIL, index, EmailLengthRange, errors)
  affiliation.strictValidator(jPath..JsonField.AFFILIATION, index, AffiliationLengthRange, errors)
  country.strictValidator(jPath..JsonField.COUNTRY, index, CountryLengthRange, errors)
}

internal fun Iterable<APIContact?>.validate(jPath: String, strict: Boolean, errors: ValidationErrors) {
  var primaries = 0
  forEachIndexed { i, c ->
    c.require(jPath, i, errors) {
      validate(jPath, i, errors, strict)

      if (isPrimary)
        primaries++

      if (primaries > 1)
        errors.add(jPath..i)
    }
  }

  if (primaries < 1) {
    firstOrNull { it != null }
      ?.isPrimary = true
  } else if (primaries > 1) {
    errors.add(jPath, "only one contact may be marked as primary")
  }
}

internal fun APIContact.toInternal() =
  DatasetContact(
    firstName   = firstName,
    middleName  = middleName,
    lastName    = lastName,
    email       = email,
    affiliation = affiliation,
    country     = country,
    isPrimary   = isPrimary
  )

internal fun Iterable<APIContact>.toInternal() = map(APIContact::toInternal)
