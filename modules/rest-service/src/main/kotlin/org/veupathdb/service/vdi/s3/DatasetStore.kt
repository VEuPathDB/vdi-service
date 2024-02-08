package org.veupathdb.service.vdi.s3

import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.lib.s3.s34k.objects.StreamObject
import org.veupathdb.service.vdi.config.Options
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDIDatasetShareOffer
import org.veupathdb.vdi.lib.common.model.VDIDatasetShareReceipt
import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.json.toJSONString
import org.veupathdb.vdi.lib.s3.datasets.paths.S3Paths
import vdi.constants.InstallZipName
import vdi.constants.UploadZipName
import java.io.InputStream
import java.util.stream.Stream

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

  fun getDatasetMeta(userID: UserID, datasetID: DatasetID): VDIDatasetMeta? {
    log.debug("fetching dataset meta file for dataset {}/{}", userID, datasetID)

    return bucket.objects.open(S3Paths.datasetMetaFile(userID, datasetID))
      ?.use { JSON.readValue<VDIDatasetMeta>(it.stream) }
  }

  fun listUploadFiles(userID: UserID, datasetID: DatasetID): List<FileEntry> {
    log.debug("fetching upload file list for dataset {}/{}", userID, datasetID)
    return bucket.objects.list(S3Paths.datasetUploadsDir(userID, datasetID))
      .map { FileEntry(it) }
  }

  fun listUserUploadFilesSizeTotals(userID: UserID): Map<DatasetID, Long> {
    log.debug("fetching upload size totals across all datasets for user {}", userID)
    val out = HashMap<DatasetID, Long>()
    bucket.objects.list(S3Paths.userDir(UserID(221741260L)))
      .asSequence()
      .filter { it.path.contains(S3Paths.UPLOAD_DIR_NAME) }
      .forEach {
        val key = it.path.getDatasetIDFromPath()
        out[key] = it.size + (out[key] ?: 0L)
      }
    return out
  }

  fun getUploadFile(userID: UserID, datasetID: DatasetID): StreamObject? {
    log.debug("fetching upload zip for dataset {}/{}", userID, datasetID)
    return bucket.objects.open(S3Paths.datasetUploadFile(userID, datasetID, UploadZipName))
  }

  fun listDataFiles(userID: UserID, datasetID: DatasetID): List<FileEntry> {
    log.debug("fetching data file list for dataset {}/{}", userID, datasetID)
    return bucket.objects.list(S3Paths.datasetDataDir(userID, datasetID))
      .map { FileEntry(it) }
  }

  fun getDataFile(userID: UserID, datasetID: DatasetID): StreamObject? {
    log.debug("fetching data zip for dataset {}/{}", userID, datasetID)
    return bucket.objects.open(S3Paths.datasetDataFile(userID, datasetID, InstallZipName))
  }

  fun getUploadsSize(userID: UserID, datasetID: DatasetID): ULong {
    log.debug("fetching user dataset upload files size total for dataset {}/{}", userID, datasetID)
    return bucket.objects.list(prefix = S3Paths.datasetUploadsDir(userID, datasetID))
      .asSequence()
      .map { it.stat() }
      .filterNotNull()
      .map { it.size }
      .sum()
      .toULong()
  }

  fun putDatasetMeta(userID: UserID, datasetID: DatasetID, meta: VDIDatasetMeta) {
    log.debug("uploading dataset meta file for dataset {}/{}", userID, datasetID)
    bucket.objects.put(S3Paths.datasetMetaFile(userID, datasetID), meta.toJSONString().byteInputStream())
  }

  fun putUserUpload(userID: UserID, datasetID: DatasetID, fn: () -> InputStream) {
    log.debug("uploading dataset user upload for dataset {}/{}", userID, datasetID)
    fn().use { bucket.objects.put(S3Paths.datasetUploadFile(userID, datasetID, UploadZipName), it) }
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
    log.debug("uploading soft-delete flag for dataset {}/{}", userID, datasetID)
    bucket.objects.touch(S3Paths.datasetDeleteFlagFile(userID, datasetID)) { overwrite = true }
  }

  fun streamAllPaths(): Stream<String> {
    return bucket.objects.stream().stream().map { it.path }
  }

  private fun String.getDatasetIDFromPath(): DatasetID {
    val it = splitToSequence('/').iterator()
    it.next() // vdi/
    it.next() // {user-id}/
    return DatasetID(it.next())
  }
}