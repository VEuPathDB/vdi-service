package vdi.lib.config.vdi

import org.veupathdb.vdi.lib.common.field.SecretString
import kotlin.time.Duration
import vdi.lib.config.common.HostAddress

data class RabbitConnectionConfig(
  val name: String?,
  val server: HostAddress,
  val username: String,
  val password: SecretString,
  val tls: Boolean?,
  val timeout: Duration?,
)
