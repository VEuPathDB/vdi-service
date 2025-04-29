package vdi.lib.config.vdi

import com.fasterxml.jackson.databind.node.ObjectNode
import vdi.lib.config.common.HostAddress
import vdi.lib.config.common.SecretString

data class DirectInstallTargetConfig(
  override val controlSchema: String,
  override val dataSchema: String,
  override val dataTypes: List<String>?,
  override val enabled: Boolean?,
  override val pass: SecretString,
  override val platform: String,
  override val poolSize: UByte?,
  override val targetName: String,
  val host: HostAddress,
): InstallTargetConfigBase
