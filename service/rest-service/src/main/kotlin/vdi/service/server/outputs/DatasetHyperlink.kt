package vdi.service.server.outputs

import vdi.service.generated.model.DatasetHyperlink
import vdi.service.generated.model.DatasetHyperlinkImpl
import org.veupathdb.vdi.lib.common.model.VDIDatasetHyperlink

internal fun DatasetHyperlink(internal: VDIDatasetHyperlink): DatasetHyperlink =
  DatasetHyperlinkImpl().apply {
    url = internal.url
    text = internal.text
    description = internal.description
    isPublication = internal.isPublication
  }
