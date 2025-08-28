@file:JvmName("LinkedDatasetOutputAdaptor")
package vdi.service.rest.server.outputs

import vdi.model.data.LinkedDataset
import vdi.service.rest.generated.model.LinkedDatasetImpl
import vdi.service.rest.generated.model.LinkedDataset as APILinkedDataset

fun LinkedDataset(link: LinkedDataset): APILinkedDataset =
  LinkedDatasetImpl().apply {
    datasetUri    = link.datasetURI.toString()
    sharesRecords = link.sharesRecords
  }
