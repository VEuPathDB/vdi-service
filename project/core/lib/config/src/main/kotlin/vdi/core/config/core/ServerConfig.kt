package vdi.core.config.core

import com.fasterxml.jackson.annotation.JsonProperty

data class ServerConfig(
  @param:JsonProperty("enableCors")
  @field:JsonProperty("enableCors")
  val enableCORS: Boolean?,
  val bindPort: UShort?,
)
