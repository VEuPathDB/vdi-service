package vdi.service.rest.server.outputs

import vdi.model.data.DatasetPublication
import vdi.service.rest.generated.model.DatasetPublication as APIPublication
import vdi.service.rest.generated.model.DatasetPublicationImpl


internal fun DatasetPublication.toExternal(): APIPublication =
  DatasetPublicationImpl().also {
    it.citation = citation
    it.pubMedId = pubmedID
  }
