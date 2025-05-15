package vdi.service.rest.server.outputs

import org.veupathdb.vdi.lib.common.model.VDIDatasetContact
import vdi.service.rest.generated.model.DatasetContact


internal fun DatasetContact(contact: VDIDatasetContact): DatasetContact =
  vdi.service.rest.generated.model.DatasetContactImpl().apply {
    name = contact.name
    email = contact.email
    affiliation = contact.affiliation
    city = contact.city
    state = contact.state
    country = contact.country
    address = contact.address
    isPrimary = contact.isPrimary
  }
