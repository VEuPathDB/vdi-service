package vdi.service.rest.server.outputs

import vdi.lib.db.cache.model.BrokenImportRecord
import vdi.lib.plugin.registry.PluginRegistry
import vdi.service.rest.generated.model.BrokenImportDetails
import vdi.service.rest.generated.model.BrokenImportDetailsImpl

fun BrokenImportDetails(record: BrokenImportRecord): BrokenImportDetails =
  BrokenImportDetailsImpl().apply {
    val datasetTypeName = PluginRegistry[record.typeName, record.typeVersion]?.displayName
      ?: throw IllegalStateException("missing dataset type name")

    datasetId = record.datasetID.toString()
    owner = record.ownerID.toLong()
    datasetType = DatasetTypeOutput(record.typeName, record.typeVersion, datasetTypeName)
    installTargets = record.projects
    messages = record.messages
  }
