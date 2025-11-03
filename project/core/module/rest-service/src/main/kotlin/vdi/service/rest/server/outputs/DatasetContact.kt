package vdi.service.rest.server.outputs

import vdi.model.data.DatasetContact
import vdi.service.rest.generated.model.DatasetContact as APIContact
import vdi.service.rest.generated.model.DatasetContactImpl


internal fun DatasetContact(contact: DatasetContact): APIContact =
  DatasetContactImpl().apply {
    firstName   = contact.firstName
    middleName  = contact.middleName
    lastName    = contact.lastName
    email       = contact.email
    affiliation = contact.affiliation
    country     = contact.country
    isPrimary   = contact.isPrimary
  }
