package vdi.service.rest.server.outputs

import vdi.core.db.cache.model.BrokenImportRecord
import vdi.core.plugin.registry.PluginRegistry
import vdi.service.rest.generated.model.BrokenImportDetails
import vdi.service.rest.generated.model.BrokenImportDetailsImpl

fun BrokenImportDetails(record: BrokenImportRecord): BrokenImportDetails =
  BrokenImportDetailsImpl().apply {
    val datasetTypeName = PluginRegistry[record.type]?.displayName
      ?: throw IllegalStateException("missing dataset type name")

    datasetId = record.datasetID.toString()
    owner = record.ownerID.toLong()
    datasetType = DatasetTypeOutput(record.type, datasetTypeName)
    installTargets = record.projects
    messages = record.messages
  }
