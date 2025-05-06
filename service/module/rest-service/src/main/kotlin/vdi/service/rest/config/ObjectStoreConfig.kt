package vdi.service.rest.config

import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.field.SecretString
import vdi.lib.config.common.HostAddress
import vdi.lib.config.vdi.ObjectStoreConfig

data class ObjectStoreConfig(
  val bucketName: BucketName,
  val host: HostAddress,
  val https: Boolean,
  val accessToken: SecretString,
  val secretKey: SecretString,
) {
  constructor(config: ObjectStoreConfig): this(
    bucketName  = BucketName(config.bucketName),
    host        = config.host,
    https       = config.https ?: true,
    accessToken = config.accessToken,
    secretKey   = config.secretKey,
  )
}
