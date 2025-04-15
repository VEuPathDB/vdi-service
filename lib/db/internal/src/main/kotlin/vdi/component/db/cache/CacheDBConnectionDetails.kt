package vdi.component.db.cache

data class CacheDBConnectionDetails(
  val host: String,
  val port: UShort,
  val name: String,
)