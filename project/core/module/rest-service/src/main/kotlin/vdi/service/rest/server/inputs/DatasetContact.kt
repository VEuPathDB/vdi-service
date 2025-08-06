@file:JvmName("DatasetContactValidator")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.model.data.DatasetContact
import vdi.service.rest.generated.model.DatasetContact as APIContact
import vdi.service.rest.generated.model.JsonField

private const val NameMinLength = 3
private const val NameMaxLength = 300
private const val EmailMinLength = 5
private const val EmailMaxLength = 4000
private const val AffiliationMaxLength = 4000
private const val CityMaxLength = 200
private const val StateMaxLength = 200
private const val CountryMaxLength = 200
private const val AddressMaxLength = 1000

internal fun APIContact.cleanup() {
  firstName   = firstName.cleanupString()
  middleName  = middleName.cleanupString()
  lastName    = lastName.cleanupString()

  isPrimary   = isPrimary ?: false

  email       = email.cleanupString()
  affiliation = affiliation.cleanupString()
  country     = country.cleanupString()

  city        = city.cleanupString()
  state       = state.cleanupString()
  address     = address.cleanupString()
}

private fun APIContact.validate(jPath: String, index: Int, errors: ValidationErrors) {
  firstName.reqCheckLength(jPath..JsonField.FIRST_NAME, index, NameMinLength, NameMaxLength, errors)
  middleName.optCheckLength(jPath..JsonField.MIDDLE_NAME, index, NameMinLength, NameMaxLength, errors)
  lastName.reqCheckLength(jPath..JsonField.LAST_NAME, index, NameMinLength, NameMaxLength, errors)

  email.optCheckLength(jPath..JsonField.EMAIL, index, EmailMinLength, EmailMaxLength, errors)
  affiliation.optCheckMaxLength(jPath..JsonField.AFFILIATION, index, AffiliationMaxLength, errors)
  city.optCheckMaxLength(jPath..JsonField.CITY, index, CityMaxLength, errors)
  state.optCheckMaxLength(jPath..JsonField.STATE, index, StateMaxLength, errors)
  country.optCheckMaxLength(jPath..JsonField.COUNTRY, index, CountryMaxLength, errors)
  address.optCheckMaxLength(jPath..JsonField.ADDRESS, index, AddressMaxLength, errors)
}

internal fun Iterable<APIContact?>.validate(jPath: String, errors: ValidationErrors) {
  var primaries = 0
  forEachIndexed { i, c ->
    c.require(jPath, i, errors) {
      validate(jPath, i, errors)

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
    city        = city,
    state       = state,
    country     = country,
    address     = address,
    isPrimary   = isPrimary
  )
