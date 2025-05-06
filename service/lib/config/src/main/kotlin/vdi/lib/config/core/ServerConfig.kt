package vdi.lib.config.core

import com.fasterxml.jackson.annotation.JsonProperty

data class ServerConfig(
  @JsonProperty("enableCors")
  val enableCORS: Boolean?,
  val bindPort: UShort?,
)
