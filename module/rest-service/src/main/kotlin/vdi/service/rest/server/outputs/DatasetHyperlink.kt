package vdi.service.rest.server.outputs

import org.veupathdb.vdi.lib.common.model.VDIDatasetHyperlink
import vdi.service.rest.generated.model.DatasetHyperlink

internal fun DatasetHyperlink(internal: VDIDatasetHyperlink): DatasetHyperlink =
  vdi.service.rest.generated.model.DatasetHyperlinkImpl().apply {
    url = internal.url
    text = internal.text
    description = internal.description
    isPublication = internal.isPublication
  }
