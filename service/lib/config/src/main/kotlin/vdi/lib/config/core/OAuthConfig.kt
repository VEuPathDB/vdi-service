package vdi.lib.config.core

import com.fasterxml.jackson.annotation.JsonProperty
import org.veupathdb.vdi.lib.common.field.SecretString

data class OAuthConfig(
  @param:JsonProperty("clientId")
  @field:JsonProperty("clientId")
  val clientID: String,
  val clientSecret: SecretString,
  val url: String,
  val keystoreFile: String?,
  val keystorePass: SecretString?,
)
