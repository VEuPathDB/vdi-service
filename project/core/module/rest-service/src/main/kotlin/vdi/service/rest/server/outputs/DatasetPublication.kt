@file:JvmName("DatasetPublicationOutputAdaptor")
package vdi.service.rest.server.outputs

import vdi.model.data.DatasetPublication
import vdi.service.rest.generated.model.DatasetPublication as APIPublication
import vdi.service.rest.generated.model.DatasetPublicationImpl


internal fun DatasetPublication(publication: DatasetPublication): APIPublication =
  DatasetPublicationImpl().also {
    it.identifier = publication.identifier
    it.type       = DatasetPublicationType(publication.type)
    it.citation   = publication.citation
    it.isPrimary  = publication.isPrimary
  }

