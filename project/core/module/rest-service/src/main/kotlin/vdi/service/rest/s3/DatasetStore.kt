package vdi.service.rest.s3

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.S3Client
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.lib.s3.s34k.objects.S3Object
import org.veupathdb.lib.s3.s34k.objects.StreamObject
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.model.data.DatasetManifest
import vdi.model.data.DatasetMetadata
import vdi.model.data.DatasetShareOffer
import vdi.model.data.DatasetShareReceipt
import vdi.json.JSON
import vdi.json.toJSONString
import java.io.InputStream
import vdi.config.raw.vdi.ObjectStoreConfig
import vdi.lib.s3.paths.ImportReadyFileName
import vdi.lib.s3.paths.S3Paths
import vdi.lib.s3.util.S3Config

object DatasetStore {
  private lateinit var client: S3Client
  private var bucketName: BucketName? = null

  fun init(config: ObjectStoreConfig) {
    client = S3Api.newClient(S3Config(config))
    bucketName = BucketName(config.bucketName)
  }

  private val bucket
    get() = try { client.buckets[bucketName!!] }
    catch (_: Throwable) { throw IllegalStateException("invalid S3 bucket name") }
      ?: throw IllegalStateException("bucket $bucketName does not exist!")

  fun getDatasetMeta(userID: UserID, datasetID: DatasetID): DatasetMetadata? {
    return bucket.objects.open(S3Paths.datasetMetaFile(userID, datasetID))
      ?.use { JSON.readValue<DatasetMetadata>(it.stream) }
  }

  fun getImportReadyZipSize(userID: UserID, datasetID: DatasetID) =
    bucket.objects[S3Paths.datasetImportReadyFile(userID, datasetID)]?.size ?: -1L

  fun listDatasetImportReadyZipSizes(userID: UserID): Map<DatasetID, Long> {
    val out = HashMap<DatasetID, Long>()

    bucket.objects.list(S3Paths.userDir(userID))
      .forEach {
        val datasetID = it.path.getDatasetIDFromPath()

        out.computeIfAbsent(datasetID) { 0 }

        if (it.path.endsWith(ImportReadyFileName))
          out[datasetID] = it.size
      }

    return out
  }

  fun getImportReadyZip(userID: UserID, datasetID: DatasetID): StreamObject? =
    bucket.objects.open(S3Paths.datasetImportReadyFile(userID, datasetID))

  fun getInstallReadyZipSize(userID: UserID, datasetID: DatasetID) =
    bucket.objects[S3Paths.datasetInstallReadyFile(userID, datasetID)]?.size ?: -1L

  fun getInstallReadyZip(userID: UserID, datasetID: DatasetID): StreamObject? =
    bucket.objects.open(S3Paths.datasetInstallReadyFile(userID, datasetID))

  fun putDatasetMeta(userID: UserID, datasetID: DatasetID, meta: DatasetMetadata) {
    bucket.objects.put(S3Paths.datasetMetaFile(userID, datasetID)) {
      contentType = "application/json"
      stream = meta.toJSONString().byteInputStream()
    }
  }

  fun putManifest(userID: UserID, datasetID: DatasetID, manifest: DatasetManifest) {
    bucket.objects.put(S3Paths.datasetManifestFile(userID, datasetID)) {
      contentType = "application/json"
      stream = manifest.toJSONString().byteInputStream()
    }
  }

  fun putImportReadyZip(userID: UserID, datasetID: DatasetID, fn: () -> InputStream) {
    fn().use { bucket.objects.put(S3Paths.datasetImportReadyFile(userID, datasetID)) {
      contentType = "application/zip"
      stream = it
    } }
  }

  fun putShareOffer(userID: UserID, datasetID: DatasetID, recipientID: UserID, offer: DatasetShareOffer) {
    bucket.objects.put(S3Paths.datasetShareOfferFile(userID, datasetID, recipientID)) {
      contentType = "application/json"
      stream = offer.toJSONString().byteInputStream()
    }
  }

  fun putShareReceipt(userID: UserID, datasetID: DatasetID, recipientID: UserID, receipt: DatasetShareReceipt) {
    bucket.objects.put(S3Paths.datasetShareReceiptFile(userID, datasetID, recipientID)) {
      contentType = "application/json"
      stream = receipt.toJSONString().byteInputStream()
    }
  }

  fun putDeleteFlag(userID: UserID, datasetID: DatasetID) {
    bucket.objects.touch(S3Paths.datasetDeleteFlagFile(userID, datasetID)) {
      contentType = "text/plain"
      overwrite = true
    }
  }

  fun listObjectsForDataset(userID: UserID, datasetID: DatasetID): Iterable<S3Object> =
    bucket.objects.list(prefix = S3Paths.datasetDir(userID, datasetID))

  fun streamAll() = bucket.objects.streamAll().stream()

  private fun String.getDatasetIDFromPath(): DatasetID {
    val it = splitToSequence('/').iterator()
    it.next() // {user-id}/
    return DatasetID(it.next())
  }
}
