package vdi.db.app

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import vdi.config.raw.common.LDAPConfig
import vdi.config.raw.vdi.InstallTargetConfig

@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(value = "vdi")
@JsonIgnoreProperties(ignoreUnknown = true)
internal data class ShortVDIConfig(
  val ldap: LDAPConfig?,
  val installTargets: Set<InstallTargetConfig>,
)
