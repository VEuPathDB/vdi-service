package vdi.lib.db.app

import org.veupathdb.vdi.lib.common.field.DataType

data class RegisteredAppDatabase(
  val databaseName: String,
  val dataType: DataType,
  val details: AppDBRegistryEntry,
)
