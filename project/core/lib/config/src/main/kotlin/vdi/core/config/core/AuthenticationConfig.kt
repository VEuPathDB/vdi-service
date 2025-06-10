package vdi.core.config.core

import vdi.model.field.SecretString

data class AuthenticationConfig(
  val adminToken: SecretString,
  val oauth: OAuthConfig,
)
