package vdi.lib.config.vdi

import vdi.lib.config.common.HostAddress
import vdi.lib.config.common.SecretString

data class ObjectStoreConfig(
  val bucketName: String,
  val host: HostAddress,
  val https: Boolean?,
  val accessToken: SecretString,
  val secretKey: SecretString,
)
