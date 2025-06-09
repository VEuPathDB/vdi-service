package vdi.service.plugin.conf

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import vdi.config.raw.common.LDAPConfig
import vdi.config.raw.vdi.InstallTargetConfig
import vdi.config.raw.vdi.PluginConfig

@JsonIgnoreProperties(ignoreUnknown = true)
data class ShortVDIConfig(
  val ldap: LDAPConfig?,
  val plugins: Map<String, PluginConfig>,
  val siteBuild: String,
  val installTargets: Set<InstallTargetConfig>,
)