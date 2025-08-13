@file:JvmName("DatasetContactValidator")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.model.data.DatasetContact
import vdi.service.rest.generated.model.DatasetContact as APIContact
import vdi.service.rest.generated.model.JsonField

private val NameLengthRange = 3..300
private val EmailLengthRange = 5..1024
private val AffiliationLengthRange = 3..4000
private val CountryLengthRange = Range3To200

private val CityLengthRange = Range3To200
private val StateLengthRange = Range3To200
private val AddressLengthRange = 5..1000

internal fun APIContact?.cleanup() = this?.apply {
  cleanupString(::getFirstName)
  cleanupString(::getMiddleName)
  cleanupString(::getLastName)

  isPrimary   = isPrimary ?: false

  cleanupString(::getEmail)
  cleanupString(::getAffiliation)
  cleanupString(::getCountry)

  cleanupString(::getCity)
  cleanupString(::getState)
  cleanupString(::getAddress)
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

  firstName.strictValidator(jPath..JsonField.FIRST_NAME, index, NameLengthRange, errors)
  middleName?.checkLength(jPath..JsonField.MIDDLE_NAME, index, NameLengthRange, errors)
  lastName.strictValidator(jPath..JsonField.LAST_NAME, index, NameLengthRange, errors)
  email.strictValidator(jPath..JsonField.EMAIL, index, EmailLengthRange, errors)
  affiliation.strictValidator(jPath..JsonField.AFFILIATION, index, AffiliationLengthRange, errors)
  country.strictValidator(jPath..JsonField.COUNTRY, index, CountryLengthRange, errors)

  city?.checkLength(jPath..JsonField.CITY, index, CityLengthRange, errors)
  state?.checkLength(jPath..JsonField.STATE, index, StateLengthRange, errors)
  address?.checkLength(jPath..JsonField.ADDRESS, index, AddressLengthRange, errors)
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

private fun APIContact.isEmpty() =
  lastName.isNullOrBlank() && email.isNullOrBlank()

internal fun APIContact.toInternal() =
  DatasetContact(
    firstName   = firstName,
    middleName  = middleName,
    lastName    = lastName,
    email       = email,
    affiliation = affiliation,
    city        = city,
    state       = state,
    country     = country,
    address     = address,
    isPrimary   = isPrimary
  )
