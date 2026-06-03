package vdi.service.rest.server.conversion

import vdi.model.meta.DatasetHyperlink as InternalType
import vdi.service.rest.generated.model.DatasetHyperlink as RamlType
import vdi.service.rest.generated.model.DatasetHyperlinkImpl

internal fun DatasetHyperlink(link: InternalType): RamlType =
  DatasetHyperlinkImpl().also {
    it.url         = link.url.toString()
    it.description = link.description
  }
