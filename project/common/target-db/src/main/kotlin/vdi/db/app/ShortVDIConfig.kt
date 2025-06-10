package vdi.db.app

import vdi.config.raw.common.LDAPConfig
import vdi.config.raw.vdi.InstallTargetConfig

internal data class ShortVDIConfig(
  val ldap: LDAPConfig?,
  val installTargets: Set<InstallTargetConfig>,
)
