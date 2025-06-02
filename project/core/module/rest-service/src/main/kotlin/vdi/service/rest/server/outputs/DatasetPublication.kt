package vdi.service.rest.server.outputs

import vdi.model.data.DatasetPublication
import vdi.service.rest.generated.model.DatasetPublication
import vdi.service.rest.generated.model.DatasetPublicationImpl


internal fun vdi.model.data.DatasetPublication.toExternal(): DatasetPublication =
  DatasetPublicationImpl().also {
    it.citation = citation
    it.pubMedId = pubmedID
  }
