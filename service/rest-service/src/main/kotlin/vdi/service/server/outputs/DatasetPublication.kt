package vdi.service.server.outputs

import vdi.service.generated.model.DatasetPublication
import vdi.service.generated.model.DatasetPublicationImpl
import org.veupathdb.vdi.lib.common.model.VDIDatasetPublication


internal fun VDIDatasetPublication.toExternal(): DatasetPublication =
  DatasetPublicationImpl().also {
    it.citation = citation
    it.pubMedId = pubmedID
  }
