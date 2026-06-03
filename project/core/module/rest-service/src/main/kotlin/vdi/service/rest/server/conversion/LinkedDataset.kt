package vdi.service.rest.server.conversion

import vdi.model.meta.LinkedDataset as InternalType
import vdi.service.rest.generated.model.LinkedDataset as RamlType
import vdi.service.rest.generated.model.LinkedDatasetImpl

internal fun LinkedDataset(link: InternalType): RamlType =
  LinkedDatasetImpl().apply {
    datasetUri    = link.datasetURI.toString()
    sharesRecords = link.sharesRecords
  }
