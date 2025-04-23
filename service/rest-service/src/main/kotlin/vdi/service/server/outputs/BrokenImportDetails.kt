package vdi.service.server.outputs

import vdi.service.generated.model.BrokenImportDetails
import vdi.service.generated.model.BrokenImportDetailsImpl
import vdi.component.db.cache.model.BrokenImportRecord
import vdi.lib.plugin.registry.PluginRegistry

fun BrokenImportDetails(record: BrokenImportRecord): BrokenImportDetails =
  BrokenImportDetailsImpl().apply {
    val datasetTypeName = PluginRegistry[record.typeName, record.typeVersion]?.displayName
      ?: throw IllegalStateException("missing dataset type name")

    datasetId = record.datasetID.toString()
    owner = record.ownerID.toLong()
    datasetType = DatasetTypeResponseBody(record.typeName, record.typeVersion, datasetTypeName)
    projectIds = record.projects
    messages = record.messages
  }
