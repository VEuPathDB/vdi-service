package vdi.component.db.app

import javax.sql.DataSource

data class AppDBRegistryEntry(
  val name: String,
  val host: String,
  val port: UShort,
  val source: DataSource,
  val dataSchema: String,
  val ctlSchema: String,
  val platform: AppDBPlatform,
)