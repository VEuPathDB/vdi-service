@file:JvmName("DatasetPublicationOutputAdaptor")
package vdi.service.rest.server.outputs

import vdi.model.meta.DatasetPublication
import vdi.service.rest.generated.model.DatasetPublicationImpl
import vdi.service.rest.generated.model.DatasetPublication as APIPublication


internal fun DatasetPublication(publication: DatasetPublication): APIPublication =
  DatasetPublicationImpl().also {
    it.identifier = publication.identifier
    it.type       = DatasetPublicationType(publication.type)
    it.citation   = publication.citation
    it.isPrimary  = publication.isPrimary
  }

