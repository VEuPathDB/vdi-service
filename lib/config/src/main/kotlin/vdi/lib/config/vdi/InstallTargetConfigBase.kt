package vdi.lib.config.vdi

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import vdi.lib.config.common.SecretString

@JsonDeserialize(using = InstallTargetDeserializer::class)
sealed interface InstallTargetConfigBase {
  val controlSchema: String
  val dataSchema: String
  val dataTypes: List<String>?
  val enabled: Boolean?
  val pass: SecretString
  val platform: String?
  val poolSize: UByte?
  val targetName: String
}
