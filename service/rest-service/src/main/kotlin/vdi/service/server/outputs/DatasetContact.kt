package vdi.service.server.outputs

import vdi.service.generated.model.DatasetContact
import vdi.service.generated.model.DatasetContactImpl
import org.veupathdb.vdi.lib.common.model.VDIDatasetContact


internal fun DatasetContact(contact: VDIDatasetContact): DatasetContact =
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
