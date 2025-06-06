package vdi.service.rest.server.outputs

import vdi.model.data.DatasetHyperlink
import vdi.service.rest.generated.model.DatasetHyperlink as APIHyperlink
import vdi.service.rest.generated.model.DatasetHyperlinkImpl

internal fun DatasetHyperlink(internal: DatasetHyperlink): APIHyperlink =
  DatasetHyperlinkImpl().apply {
    url = internal.url.toString()
    text = internal.text
    description = internal.description
    isPublication = internal.isPublication
  }
