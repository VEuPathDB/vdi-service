package vdi.core.config.rabbit

import kotlin.time.Duration
import vdi.config.parse.fields.PartialHostAddress
import vdi.model.field.SecretString

data class RabbitConnectionConfig(
  val name: String?,
  val server: PartialHostAddress,
  val username: String,
  val password: SecretString,
  val tls: Boolean?,
  val timeout: Duration?,
)
