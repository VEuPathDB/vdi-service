package org.veupathdb.vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import java.time.OffsetDateTime

data class DatasetImpl(
  override val datasetID: DatasetID,
  override val typeName: String,
  override val typeVersion: String,
  override val ownerID: UserID,
  override val isDeleted: Boolean,
  override val created: OffsetDateTime,
  override val importStatus: DatasetImportStatus
) : Dataset