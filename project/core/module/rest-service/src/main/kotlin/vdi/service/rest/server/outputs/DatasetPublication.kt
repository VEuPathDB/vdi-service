package vdi.service.rest.server.outputs

import vdi.model.data.DatasetPublication
import vdi.model.data.DatasetPublication.PublicationType
import vdi.service.rest.generated.model.DatasetPublication as APIPublication
import vdi.service.rest.generated.model.DatasetPublicationImpl


internal fun DatasetPublication(publication: DatasetPublication): APIPublication =
  DatasetPublicationImpl().also {
    it.identifier = publication.identifier
    it.type       = PublicationType(publication.type)
    it.citation   = publication.citation
    it.isPrimary  = publication.isPrimary
  }

internal fun PublicationType(type: PublicationType): APIPublication.TypeType =
  when (type) {
    PublicationType.PubMed -> APIPublication.TypeType.PMID
    PublicationType.DOI    -> APIPublication.TypeType.DOI
  }


internal fun DatasetPublication.toExternal(): APIPublication =
  DatasetPublicationImpl().also {
    it.citation = citation
    it.pubMedId = identifier
  }
