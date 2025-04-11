package org.veupathdb.service.vdi.server.inputs

import org.veupathdb.lib.request.validation.*
import org.veupathdb.service.vdi.generated.model.DatasetContact
import org.veupathdb.service.vdi.generated.model.JsonField
import org.veupathdb.vdi.lib.common.model.VDIDatasetContact

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

internal fun Collection<DatasetContact?>.validate(jPath: String, errors: ValidationErrors) {
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
  VDIDatasetContact(name, email, affiliation, city, state, country, address, isPrimary)
