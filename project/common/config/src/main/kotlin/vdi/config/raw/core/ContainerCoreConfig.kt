package vdi.config.raw.core

import vdi.config.raw.common.LDAPConfig


data class ContainerCoreConfig(
  val ldap: LDAPConfig?,
  val authentication: AuthenticationConfig,
  val http: ServerConfig?,
  val databases: CoreDatabaseSet?,
)
