package org.veupathdb.service.vdi.server.outputs

import org.veupathdb.service.vdi.generated.model.DatasetContact
import org.veupathdb.service.vdi.generated.model.DatasetContactImpl
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
