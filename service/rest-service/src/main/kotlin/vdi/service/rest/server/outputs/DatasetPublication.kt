package vdi.service.rest.server.outputs

import vdi.service.rest.generated.model.DatasetPublication
import vdi.service.rest.generated.model.DatasetPublicationImpl
import org.veupathdb.vdi.lib.common.model.VDIDatasetPublication


internal fun VDIDatasetPublication.toExternal(): vdi.service.rest.generated.model.DatasetPublication =
  vdi.service.rest.generated.model.DatasetPublicationImpl().also {
    it.citation = citation
    it.pubMedId = pubmedID
  }
