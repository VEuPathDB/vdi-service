package vdi.lib.config.vdi

import vdi.lib.config.common.HostAddress
import vdi.lib.config.common.SecretString

data class RabbitConnectionConfig(
  val name: String?,
  val host: HostAddress,
  val user: String,
  val pass: SecretString,
  val tls: Boolean?,
)
