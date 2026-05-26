package vdi.service.rest.server.outputs

import vdi.core.db.cache.model.BrokenImportRecord
import vdi.service.rest.generated.model.BrokenImportDetails
import vdi.service.rest.generated.model.BrokenImportDetailsImpl

fun BrokenImportDetails(record: BrokenImportRecord): BrokenImportDetails =
  BrokenImportDetailsImpl().apply {
    datasetId = record.datasetID.toString()
    owner = record.ownerID.toLong()
    datasetType = record.type.toExternal()
    installTargets = record.projects
    messages = record.messages
  }
