package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.util.ValidationErrors
import org.veupathdb.vdi.lib.common.model.VDIDatasetContact
import vdi.component.db.app.*

internal fun DatasetContact.cleanup() {
  isPrimary = isPrimary ?: false
  name = name?.takeIf { it.isNotBlank() }
    ?.trim()
  email = email?.takeIf { it.isNotBlank() }
    ?.trim()
  affiliation = affiliation?.takeIf { it.isNotBlank() }
    ?.trim()
  city = city?.takeIf { it.isNotBlank() }
    ?.trim()
  state = state?.takeIf { it.isNotBlank() }
    ?.trim()
  country = country?.takeIf { it.isNotBlank() }
    ?.trim()
  address = address?.takeIf { it.isNotBlank() }
    ?.trim()
}

internal fun DatasetContact?.validate(index: Int, validationErrors: ValidationErrors) {
  if (this == null) {
    validationErrors.add("meta.contacts[$index]", "entries must not be null")
    return
  }

  if (name == null)
    validationErrors.add("meta.contacts[$index].name", "field is required")
  else
    name.checkLength({ "meta.contacts[$index].name" }, 3, DatasetContactNameMaxLength, validationErrors)

  email?.checkLength({ "meta.contacts[$index].email" }, 5, DatasetContactEmailMaxLength, validationErrors)
  affiliation?.checkLength({ "meta.contacts[$index].affiliation" }, DatasetContactAffiliationMaxLength, validationErrors)
  city?.checkLength({ "meta.contacts[$index].city" }, DatasetContactCityMaxLength, validationErrors)
  state?.checkLength({ "meta.contacts[$index].state" }, DatasetContactStateMaxLength, validationErrors)
  country?.checkLength({ "meta.contacts[$index].country" }, DatasetContactCountryMaxLength, validationErrors)
  address?.checkLength({ "meta.contacts[$index].address" }, DatasetContactAddressMaxLength, validationErrors)
}

internal fun DatasetContact.toInternal() =
  VDIDatasetContact(name, email, affiliation, city, state, country, address, isPrimary)

internal fun VDIDatasetContact.toExternal(): DatasetContact =
  DatasetContactImpl().also {
    it.name = name
    it.email = email
    it.affiliation = affiliation
    it.city = city
    it.state = state
    it.country = country
    it.address = address
    it.isPrimary = isPrimary
  }
