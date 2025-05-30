package vdi.lib.db.app

import javax.sql.DataSource

data class AppDBRegistryEntry(
  val name: String,
  val host: String,
  val port: UShort,
  val source: DataSource,
  val ctlSchema: String,
  val platform: AppDBPlatform,
)

