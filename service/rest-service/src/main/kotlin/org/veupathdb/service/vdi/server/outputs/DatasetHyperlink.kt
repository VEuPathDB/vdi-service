package org.veupathdb.service.vdi.server.outputs

import org.veupathdb.service.vdi.generated.model.DatasetHyperlink
import org.veupathdb.service.vdi.generated.model.DatasetHyperlinkImpl
import org.veupathdb.vdi.lib.common.model.VDIDatasetHyperlink

internal fun DatasetHyperlink(internal: VDIDatasetHyperlink): DatasetHyperlink =
  DatasetHyperlinkImpl().apply {
    url = internal.url
    text = internal.text
    description = internal.description
    isPublication = internal.isPublication
  }
