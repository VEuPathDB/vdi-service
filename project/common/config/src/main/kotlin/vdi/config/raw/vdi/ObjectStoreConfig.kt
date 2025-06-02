package vdi.config.raw.vdi

import vdi.config.parse.fields.PartialHostAddress
import vdi.model.field.SecretString


data class ObjectStoreConfig(
  val bucketName: String,
  val server: PartialHostAddress,
  val https: Boolean?,
  val accessToken: SecretString,
  val secretKey: SecretString,
)
