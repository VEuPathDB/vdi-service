@file:JvmName("DatasetContactValidator")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.model.data.DatasetContact
import vdi.model.data.DatasetContact
import vdi.service.rest.generated.model.DatasetContact
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

internal fun DatasetContact.cleanup() {
  isPrimary   = isPrimary ?: false
  name        = name.cleanupString()
  email       = email.cleanupString()
  affiliation = affiliation.cleanupString()
  city        = city.cleanupString()
  state       = state.cleanupString()
  country     = country.cleanupString()
  address     = address.cleanupString()
}

private fun DatasetContact.validate(jPath: String, index: Int, errors: ValidationErrors) {
  name.reqCheckLength(jPath..JsonField.NAME, index, NameMinLength, NameMaxLength, errors)

  email.optCheckLength(jPath..JsonField.EMAIL, index, EmailMinLength, EmailMaxLength, errors)
  affiliation.optCheckMaxLength(jPath..JsonField.AFFILIATION, index, AffiliationMaxLength, errors)
  city.optCheckMaxLength(jPath..JsonField.CITY, index, CityMaxLength, errors)
  state.optCheckMaxLength(jPath..JsonField.STATE, index, StateMaxLength, errors)
  country.optCheckMaxLength(jPath..JsonField.COUNTRY, index, CountryMaxLength, errors)
  address.optCheckMaxLength(jPath..JsonField.ADDRESS, index, AddressMaxLength, errors)
}

internal fun Iterable<DatasetContact?>.validate(jPath: String, errors: ValidationErrors) {
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

internal fun DatasetContact.toInternal() =
  DatasetContact(
    name        = name,
    email       = email,
    affiliation = affiliation,
    city        = city,
    state       = state,
    country     = country,
    address     = address,
    isPrimary   = isPrimary
  )
