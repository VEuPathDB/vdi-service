package vdi.core.db.app

import javax.sql.DataSource
import vdi.db.app.TargetDatabaseDetails

data class TargetDatabaseReference(
  val identifier: String,
  val details: TargetDatabaseDetails,
  val dataSource: DataSource,
)