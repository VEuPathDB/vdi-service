package vdi.lib.config.vdi

import org.veupathdb.vdi.lib.common.field.SecretString
import vdi.lib.config.common.HostAddress

data class ObjectStoreConfig(
  val bucketName: String,
  val server: HostAddress,
  val https: Boolean?,
  val accessToken: SecretString,
  val secretKey: SecretString,
)
