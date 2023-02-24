package vdi.components.datasets

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import java.io.InputStream
import vdi.components.datasets.model.*
import vdi.components.datasets.paths.S3DatasetPathFactory
import vdi.components.json.JSON

internal class DatasetDirectoryImpl(
  override val ownerID: String,
  override val datasetID: String,
  private val bucket: S3Bucket,
  private val pathFactory: S3DatasetPathFactory,
) : DatasetDirectory {

  override fun exists(): Boolean =
    bucket.objects.listSubPaths(pathFactory.datasetDir()).count > 0

  override fun hasMeta() =
    pathFactory.datasetMetaFile() in bucket.objects

  override fun getMeta() =
    DatasetMetaFileImpl(bucket, pathFactory.datasetMetaFile())

  override fun putMeta(meta: DatasetMeta) {
    bucket.objects.put(pathFactory.datasetMetaFile(), JSON.writeValueAsBytes(meta).inputStream())
  }


  override fun hasManifest() =
    pathFactory.datasetManifestFile() in bucket.objects

  override fun getManifest() =
    DatasetManifestFileImpl(bucket, pathFactory.datasetManifestFile())

  override fun putManifest(manifest: DatasetManifest) {
    bucket.objects.put(pathFactory.datasetManifestFile(), JSON.writeValueAsBytes(manifest).inputStream())
  }


  override fun hasDeleteFlag() =
    pathFactory.datasetDeleteFlagFile() in bucket.objects

  override fun getDeleteFlag() =
    DatasetDeleteFlagFileImpl(bucket, pathFactory.datasetDeleteFlagFile())

  override fun putDeleteFlag() {
    bucket.objects.touch(pathFactory.datasetDeleteFlagFile())
  }


  override fun getUploadFiles() =
    bucket.objects.list(pathFactory.datasetUploadsDir())
      .map { DatasetUploadFileImpl(bucket, it.path) }

  override fun putUploadFile(name: String, fn: () -> InputStream) {
    fn().use { bucket.objects.put(pathFactory.datasetUploadFile(name), it) }
  }


  override fun getDataFiles() =
    bucket.objects.list(pathFactory.datasetDataDir())
      .map { DatasetDataFileImpl(bucket, it.path) }

  override fun putDataFile(name: String, fn: () -> InputStream) {
    fn().use { bucket.objects.put(pathFactory.datasetDataFile(name), it) }
  }

  override fun getShares(): Map<String, DatasetShare> {
    val pathPrefix = pathFactory.datasetSharesDir()
    val subPaths   = bucket.objects.listSubPaths(pathPrefix).commonPrefixes()
    val retValue   = HashMap<String, DatasetShare>(subPaths.size)

    subPaths.forEach {
      val recipientID = it.substring(pathPrefix.length)

      retValue[recipientID] = DatasetShareImpl(
        recipientID,
        DatasetShareOfferFileImpl(bucket, pathFactory.datasetShareOfferFile(recipientID)),
        DatasetShareReceiptFileImpl(bucket, pathFactory.datasetShareReceiptFile(recipientID)),
      )
    }

    return retValue
  }

  override fun putShare(recipientID: String) {
    val offer   = DatasetShareOffer(DatasetShareOfferAction.Grant)
    val receipt = DatasetShareReceipt(DatasetShareReceiptAction.Accept)

    bucket.objects.put(pathFactory.datasetShareOfferFile(recipientID), JSON.writeValueAsBytes(offer).inputStream())
    bucket.objects.put(pathFactory.datasetShareReceiptFile(recipientID), JSON.writeValueAsBytes(receipt).inputStream())
  }
}