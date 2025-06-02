package vdi.core.db.cache

import org.postgresql.PGProperty
import vdi.config.raw.vdi.CacheDBConfig

data class CacheDBConnectionDetails(
  val host: String,
  val port: UShort,
  val name: String,
) {
  constructor(conf: CacheDBConfig): this(
    host = conf.server.host,
    port = conf.server.port ?: PGProperty.PG_PORT.defaultValue!!.toUShort(),
    name = conf.name ?: "vdi",
  )
}
