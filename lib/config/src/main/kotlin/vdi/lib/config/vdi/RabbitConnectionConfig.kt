package vdi.lib.config.vdi

import org.veupathdb.vdi.lib.common.field.SecretString
import org.veupathdb.vdi.lib.config.PartialHostAddress
import kotlin.time.Duration

data class RabbitConnectionConfig(
  val name: String?,
  val server: PartialHostAddress,
  val username: String,
  val password: SecretString,
  val tls: Boolean?,
  val timeout: Duration?,
)
