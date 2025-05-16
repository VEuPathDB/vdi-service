package vdi.lib.rabbit

import org.veupathdb.vdi.lib.common.field.SecretString
import org.veupathdb.vdi.lib.common.util.HostAddress
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import vdi.lib.config.vdi.RabbitConnectionConfig

data class ConnectionConfig(
  val address: HostAddress,
  val username: String,
  val password: SecretString,
  val connectionName: String?,
  val useTLS: Boolean,
  val connectionTimeout: Duration,
) {
  constructor(conf: RabbitConnectionConfig): this(
    address = conf.server.toHostAddress(if (conf.tls ?: DefaultUseTLS) DefaultTLSPort else DefaultStandardPort),
    username = conf.username,
    password = conf.password,
    connectionName = conf.name,
    useTLS = conf.tls ?: DefaultUseTLS,
    connectionTimeout = conf.timeout ?: DefaultConnectionTimeout
  )

  companion object {
    inline val DefaultStandardPort
      get(): UShort = 5672u
    inline val DefaultTLSPort
      get(): UShort = 5671u

    inline val DefaultUseTLS
      get() = true

    inline val DefaultConnectionTimeout
      get() = 10.seconds
  }
}
