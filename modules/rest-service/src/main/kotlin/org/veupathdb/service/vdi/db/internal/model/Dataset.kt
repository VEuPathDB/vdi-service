package org.veupathdb.service.vdi.db.internal.model

import java.time.OffsetDateTime
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

interface Dataset {
  val datasetID: DatasetID
  val typeName: String
  val typeVersion: String
  val ownerID: UserID
  val isDeleted: Boolean
  val created: OffsetDateTime
}