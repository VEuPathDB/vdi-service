package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.model.meta.DatasetContact
import vdi.service.rest.generated.model.JsonField
import vdi.service.rest.generated.model.DatasetContact as APIContact

private inline val ContactNameLengthRange get() = 2..300
private inline val EmailLengthRange get() = 5..1024
private inline val AffiliationLengthRange get() = 3..4000
private inline val CountryLengthRange get() = 3..200

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
  errors: ValidationErrors,
  strict: Boolean = false,
) {
  // conditionally require "strict" fields
  val strictValidator: String.(String, IntRange, ValidationErrors) -> Unit =
    if (strict)
      String::reqCheckLength
    else
      String::checkLength

  firstName?.reqCheckLength(jPath..JsonField.FIRST_NAME, ContactNameLengthRange, errors)
  middleName?.checkLength(jPath..JsonField.MIDDLE_NAME, 0..300, errors)
  lastName?.reqCheckLength(jPath..JsonField.LAST_NAME, ContactNameLengthRange, errors)
  email?.strictValidator(jPath..JsonField.EMAIL, EmailLengthRange, errors)
  affiliation?.strictValidator(jPath..JsonField.AFFILIATION, AffiliationLengthRange, errors)
  country?.strictValidator(jPath..JsonField.COUNTRY, CountryLengthRange, errors)
}

internal fun Iterable<APIContact?>.validate(jPath: String, strict: Boolean, errors: ValidationErrors) {
  var primaries = 0
  forEachIndexed { i, c ->
    c.require(jPath, i, errors) {
      validate(jPath..i, errors, strict)

      if (isPrimary)
        primaries++
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
