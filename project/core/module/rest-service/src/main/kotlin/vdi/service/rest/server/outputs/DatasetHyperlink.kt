package vdi.service.rest.server.outputs

import vdi.model.data.DatasetHyperlink
import vdi.service.rest.generated.model.DatasetHyperlink
import vdi.service.rest.generated.model.DatasetHyperlinkImpl

internal fun DatasetHyperlink(internal: DatasetHyperlink): DatasetHyperlink =
  DatasetHyperlinkImpl().apply {
    url = internal.url
    text = internal.text
    description = internal.description
    isPublication = internal.isPublication
  }
