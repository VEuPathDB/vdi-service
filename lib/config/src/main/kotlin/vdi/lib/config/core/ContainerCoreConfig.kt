package vdi.lib.config.core

import vdi.lib.config.common.LDAPConfig

data class ContainerCoreConfig(
  val ldap: LDAPConfig?,
  val authentication: AuthenticationConfig,
  val http: ServerConfig?,
  val databases: CoreDatabaseSet?,
)
