package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.BrokenImportDetails
import org.veupathdb.service.vdi.generated.model.BrokenImportDetailsImpl
import vdi.component.db.cache.model.BrokenImportRecord
import vdi.component.plugin.mapping.PluginHandlers

fun BrokenImportDetails(record: BrokenImportRecord): BrokenImportDetails =
  BrokenImportDetailsImpl().apply {
    val datasetTypeName = PluginHandlers[record.typeName, record.typeVersion]?.displayName
      ?: throw IllegalStateException("missing dataset type name")

    datasetId = record.datasetID.toString()
    owner = record.ownerID.toLong()
    datasetType = DatasetTypeInfo(record.typeName, record.typeVersion, datasetTypeName)
    projectIds = record.projects
    messages = record.messages
  }
