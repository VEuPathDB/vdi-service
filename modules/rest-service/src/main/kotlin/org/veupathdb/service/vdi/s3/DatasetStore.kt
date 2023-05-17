package org.veupathdb.service.vdi.s3

import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.service.vdi.config.Options
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDIDatasetShareOffer
import org.veupathdb.vdi.lib.common.model.VDIDatasetShareReceipt
import org.veupathdb.vdi.lib.json.toJSONString
import org.veupathdb.vdi.lib.s3.datasets.paths.S3Paths
import java.io.InputStream

private const val UPLOAD_FILE_NAME = "upload.tar.gz"

object DatasetStore {

  private val log = LoggerFactory.getLogger(javaClass)

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

  fun putDatasetMeta(userID: UserID, datasetID: DatasetID, meta: VDIDatasetMeta) {
    log.debug("uploading dataset meta file for user {}, dataset {}", userID, datasetID)
    bucket.objects.put(S3Paths.datasetMetaFile(userID, datasetID), meta.toJSONString().byteInputStream())
  }

  fun putUserUpload(userID: UserID, datasetID: DatasetID, fn: () -> InputStream) {
    log.debug("uploading dataset user upload for user {}, dataset {}", userID, datasetID)
    fn().use { bucket.objects.put(S3Paths.datasetUploadFile(userID, datasetID, UPLOAD_FILE_NAME), it) }
  }

  fun putShareOffer(userID: UserID, datasetID: DatasetID, recipientID: UserID, offer: VDIDatasetShareOffer) {
    log.debug("uploading share offer for owner {}, dataset {}, recipient {}, action {}", userID, datasetID, recipientID, offer.action)
    bucket.objects.put(S3Paths.datasetShareOfferFile(userID, datasetID, recipientID), offer.toJSONString().byteInputStream())
  }

  fun putShareReceipt(userID: UserID, datasetID: DatasetID, recipientID: UserID, receipt: VDIDatasetShareReceipt) {
    log.debug("uploading share receipt for owner {}, dataset {}, recipient {}, action {}", userID, datasetID, recipientID, receipt.action)
    bucket.objects.put(S3Paths.datasetShareReceiptFile(userID, datasetID, recipientID), receipt.toJSONString().byteInputStream())
  }

  fun putDeleteFlag(userID: UserID, datasetID: DatasetID) {
    log.debug("uploading soft-delete flag for owner {}, dataset {}", userID, datasetID)
    bucket.objects.touch(S3Paths.datasetDeleteFlagFile(userID, datasetID))
  }
}