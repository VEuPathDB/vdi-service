package vdi.lib.config.core

import org.veupathdb.vdi.lib.config.LDAPConfig

data class ContainerCoreConfig(
  val ldap: LDAPConfig?,
  val authentication: AuthenticationConfig,
  val http: ServerConfig?,
  val databases: CoreDatabaseSet?,
)
