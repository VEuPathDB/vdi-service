package org.veupathdb.service.vdi.s3

import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.service.vdi.config.Options
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.s3.datasets.paths.S3Paths
import java.io.InputStream

private const val UPLOAD_FILE_NAME = "upload.tar.gz"

object DatasetStore {
  private val client = S3Api.newClient(S3Config(
    url       = Options.S3.host,
    port      = Options.S3.port,
    secure    = Options.S3.useHttps,
    accessKey = Options.S3.accessToken,
    secretKey = Options.S3.secretKey,
  ))

  private val bucket
    get() = try { client.buckets[BucketName(Options.S3.bucketName)] }
    catch (e: Throwable) { throw IllegalStateException("invalid S3 bucket name") }
      ?: throw IllegalStateException("bucket ${Options.S3.bucketName} does not exist!")

  fun putUserUpload(userID: UserID, datasetID: DatasetID, fn: () -> InputStream) {
    fn().use { bucket.objects.put(S3Paths.datasetUploadFile(userID, datasetID, UPLOAD_FILE_NAME), it) }
  }
}