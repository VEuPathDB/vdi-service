package vdi.service.rest.server.outputs

import vdi.service.rest.generated.model.DatasetContact
import vdi.service.rest.generated.model.DatasetContactImpl
import org.veupathdb.vdi.lib.common.model.VDIDatasetContact


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
