package vdi.lib.config.core

import org.veupathdb.vdi.lib.common.field.SecretString

data class AuthenticationConfig(val adminToken: SecretString?, val oauth: OAuthConfig?)
