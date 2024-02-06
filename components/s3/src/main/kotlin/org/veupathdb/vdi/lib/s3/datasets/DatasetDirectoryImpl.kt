package org.veupathdb.vdi.lib.s3.datasets

import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toUserIDOrNull
import org.veupathdb.vdi.lib.common.model.*
import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.s3.datasets.files.*
import org.veupathdb.vdi.lib.s3.datasets.paths.S3DatasetPathFactory
import java.io.InputStream

internal class DatasetDirectoryImpl(
  override val ownerID: UserID,
  override val datasetID: DatasetID,
  private val bucket: S3Bucket,
  private val pathFactory: S3DatasetPathFactory,
) : DatasetDirectory {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun exists(): Boolean =
    bucket.objects.listSubPaths(pathFactory.datasetDir()).count > 0


  override fun hasMeta() =
    pathFactory.datasetMetaFile() in bucket.objects

  override fun getMeta() =
    DatasetMetaFileImpl(bucket, pathFactory.datasetMetaFile())

  override fun putMeta(meta: VDIDatasetMeta) {
    bucket.objects[pathFactory.datasetMetaFile()] = JSON.writeValueAsBytes(meta).inputStream()
  }


  override fun hasManifest() =
    pathFactory.datasetManifestFile() in bucket.objects

  override fun getManifest() =
    DatasetManifestFileImpl(bucket, pathFactory.datasetManifestFile())

  override fun putManifest(manifest: VDIDatasetManifest) {
    bucket.objects[pathFactory.datasetManifestFile()] = JSON.writeValueAsBytes(manifest).inputStream()
  }


  override fun hasDeleteFlag() =
    pathFactory.datasetDeleteFlagFile() in bucket.objects

  override fun getDeleteFlag() =
    DatasetDeleteFlagFileImpl(bucket, pathFactory.datasetDeleteFlagFile())

  override fun putDeleteFlag() {
    bucket.objects.touch(pathFactory.datasetDeleteFlagFile())
  }


  override fun hasUploadFile() =
    pathFactory.datasetUploadZip() in bucket.objects

  override fun getUploadFile() =
    DatasetRawUploadFileImpl(bucket, pathFactory.datasetDeleteFlagFile())

  override fun putUploadFile(fn: () -> InputStream) {
    fn().use { bucket.objects[pathFactory.datasetUploadZip()] = it.buffered() }
  }


  override fun hasImportReadyFile() =
    pathFactory.datasetImportReadyZip() in bucket.objects

  override fun getImportReadyFile() =
    DatasetImportableFileImpl(bucket, pathFactory.datasetImportReadyZip())

  override fun putImportReadyFile(fn: () -> InputStream) {
    fn().use { bucket.objects[pathFactory.datasetImportReadyZip()] = it.buffered() }
  }


  override fun hasInstallReadyFile() =
    pathFactory.datasetInstallReadyZip() in bucket.objects

  override fun getInstallReadyFile() =
    DatasetInstallableFileImpl(bucket, pathFactory.datasetInstallReadyZip())

  override fun putInstallReadyFile(fn: () -> InputStream) {
    fn().use { bucket.objects[pathFactory.datasetInstallReadyZip()] = it.buffered() }
  }

  override fun getShares(): Map<UserID, DatasetShare> {
    log.debug("looking up shares for dataset {} owned by user {}", datasetID, ownerID)

    val pathPrefix = pathFactory.datasetSharesDir()
    val subPaths   = bucket.objects.listSubPaths(pathPrefix).commonPrefixes()
    val retValue   = HashMap<UserID, DatasetShare>(subPaths.size)

    subPaths.forEach {
      val recipientID = it.substring(pathPrefix.length)
        .trim('/')
        .toUserIDOrNull()
        ?: throw IllegalStateException("invalid user ID")

      retValue[recipientID] = DatasetShareImpl(
        recipientID,
        DatasetShareOfferFileImpl(bucket, pathFactory.datasetShareOfferFile(recipientID)),
        DatasetShareReceiptFileImpl(bucket, pathFactory.datasetShareReceiptFile(recipientID)),
      )
    }

    return retValue
  }

  override fun putShare(recipientID: UserID) {
    log.debug("putting a new share for user {} into dataset {} owned by user {}", recipientID, datasetID, ownerID)

    val offer   = VDIDatasetShareOffer(VDIShareOfferAction.Grant)
    val receipt = VDIDatasetShareReceipt(VDIShareReceiptAction.Accept)

    bucket.objects.put(pathFactory.datasetShareOfferFile(recipientID), JSON.writeValueAsBytes(offer).inputStream())
    bucket.objects.put(pathFactory.datasetShareReceiptFile(recipientID), JSON.writeValueAsBytes(receipt).inputStream())
  }
}