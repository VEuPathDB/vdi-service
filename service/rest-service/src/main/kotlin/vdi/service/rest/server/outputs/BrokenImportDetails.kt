package vdi.service.rest.server.outputs

import vdi.service.rest.generated.model.BrokenImportDetails
import vdi.service.rest.generated.model.BrokenImportDetailsImpl
import vdi.component.db.cache.model.BrokenImportRecord
import vdi.lib.plugin.registry.PluginRegistry

fun BrokenImportDetails(record: BrokenImportRecord): vdi.service.rest.generated.model.BrokenImportDetails =
  vdi.service.rest.generated.model.BrokenImportDetailsImpl().apply {
    val datasetTypeName = PluginRegistry[record.typeName, record.typeVersion]?.displayName
      ?: throw IllegalStateException("missing dataset type name")

    datasetId = record.datasetID.toString()
    owner = record.ownerID.toLong()
    datasetType = DatasetTypeResponseBody(record.typeName, record.typeVersion, datasetTypeName)
    projectIds = record.projects
    messages = record.messages
  }
