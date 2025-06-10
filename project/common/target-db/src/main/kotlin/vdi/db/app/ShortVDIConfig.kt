package vdi.db.app

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import vdi.config.raw.common.LDAPConfig
import vdi.config.raw.vdi.InstallTargetConfig

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class ShortVDIConfig(
  val ldap: LDAPConfig?,
  val installTargets: Set<InstallTargetConfig>,
)

