package vdi.lib.config.vdi

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.lib.config.common.SecretString

data class LDAPInstallTargetConfig(
  override val controlSchema: String,
  override val dataSchema: String,
  override val dataTypes: List<String>?,
  override val enabled: Boolean?,
  override val pass: SecretString,
  override val platform: String?,
  override val poolSize: UByte?,
  override val targetName: String,
  @JsonProperty("ldapCn")
  val ldapCN: String,
): InstallTargetConfigBase
