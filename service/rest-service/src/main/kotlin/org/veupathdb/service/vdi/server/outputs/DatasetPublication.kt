package org.veupathdb.service.vdi.server.outputs

import org.veupathdb.service.vdi.generated.model.DatasetPublication
import org.veupathdb.service.vdi.generated.model.DatasetPublicationImpl
import org.veupathdb.vdi.lib.common.model.VDIDatasetPublication


internal fun VDIDatasetPublication.toExternal(): DatasetPublication =
  DatasetPublicationImpl().also {
    it.citation = citation
    it.pubMedId = pubmedID
  }
