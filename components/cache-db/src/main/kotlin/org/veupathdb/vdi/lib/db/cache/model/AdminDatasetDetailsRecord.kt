package org.veupathdb.vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import java.time.OffsetDateTime

data class AdminDatasetDetailsRecord(
    val datasetID: DatasetID,
    val ownerID: UserID,
    val origin: String,
    val created: OffsetDateTime,
    val typeName: String,
    val typeVersion: String,
    val name: String,
    val summary: String?,
    val description: String?,
    val sourceURL: String?,
    val visibility: VDIDatasetVisibility,
    val projectIDs: List<ProjectID>,
    val syncControl: VDISyncControlRecord?,
    val importStatus: DatasetImportStatus,
    val importMessage: String?,
    val messages: List<String>,
    val installFiles: List<String>,
    val uploadFiles: List<String>
)