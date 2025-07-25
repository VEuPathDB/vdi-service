package vdi.service.rest.server.outputs

import vdi.model.data.DatasetContact
import vdi.service.rest.generated.model.DatasetContact as APIContact
import vdi.service.rest.generated.model.DatasetContactImpl


internal fun DatasetContact(contact: DatasetContact): APIContact =
  DatasetContactImpl().apply {
    name = contact.name
    email = contact.email
    affiliation = contact.affiliation
    city = contact.city
    state = contact.state
    country = contact.country
    address = contact.address
    isPrimary = contact.isPrimary
  }
