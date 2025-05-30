package vdi.lib.config.vdi

import org.veupathdb.vdi.lib.common.field.SecretString
import org.veupathdb.vdi.lib.config.PartialHostAddress

data class ObjectStoreConfig(
  val bucketName: String,
  val server: PartialHostAddress,
  val https: Boolean?,
  val accessToken: SecretString,
  val secretKey: SecretString,
)
