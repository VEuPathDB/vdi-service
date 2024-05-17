package vdi.component.db.app.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import java.time.OffsetDateTime

class FailedInstallRecord(
  val datasetID: DatasetID,
  val ownerID: UserID,
  val typeName: String,
  val typeVersion: String,
  val installType: InstallType,
  val installMessage: String,
  val updated: OffsetDateTime,
)