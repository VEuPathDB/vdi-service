package vdi.service.rest.server.outputs

import org.veupathdb.vdi.lib.common.model.VDIDatasetPublication
import vdi.service.rest.generated.model.DatasetPublication


internal fun VDIDatasetPublication.toExternal(): DatasetPublication =
  vdi.service.rest.generated.model.DatasetPublicationImpl().also {
    it.citation = citation
    it.pubMedId = pubmedID
  }
