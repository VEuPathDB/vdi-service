package vdi.config.raw.core

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.field.SecretString

data class OAuthConfig(
  @param:JsonProperty("clientId")
  @field:JsonProperty("clientId")
  val clientID: String,
  val clientSecret: SecretString,
  val url: String,
  val keystoreFile: String?,
  val keystorePass: SecretString?,
)
