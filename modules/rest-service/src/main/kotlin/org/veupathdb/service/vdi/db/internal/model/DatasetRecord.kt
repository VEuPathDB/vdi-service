package org.veupathdb.service.vdi.db.internal.model

import java.time.OffsetDateTime
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

data class DatasetRecord(
  override val datasetID: DatasetID,
  override val typeName: String,
  override val typeVersion: String,
  override val ownerID: UserID,
  override val isDeleted: Boolean,
  override val created: OffsetDateTime,
  override val name: String,
  override val summary: String?,
  override val description: String?,
  override val files: Collection<String>,
  override val projects: Collection<String>
) : Dataset, DatasetMeta, DatasetFileLinks, DatasetProjectLinks