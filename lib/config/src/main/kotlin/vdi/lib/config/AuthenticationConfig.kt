package vdi.lib.config

import vdi.lib.config.common.SecretString

data class AuthenticationConfig(
  val adminToken: SecretString?,
  val oauth: OAuthConfig?,
)
