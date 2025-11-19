package vdi.core.db.app

import vdi.model.meta.DatasetType

data class RegisteredAppDatabase(
  val databaseName: String,
  val dataType: DatasetType,
  val details: TargetDatabaseReference,
)
