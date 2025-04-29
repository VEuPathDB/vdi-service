package vdi.lib.config

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.lib.config.common.SecretString

data class OAuthConfig(
  @JsonProperty("clientId")
  val clientID: String,
  val clientSecret: SecretString,
  val url: String,
  val keystoreFile: String?,
  val keystorePass: SecretString?,
)
