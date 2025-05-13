package vdi.lib.s3

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toUserIDOrNull
import org.veupathdb.vdi.lib.common.model.*
import org.veupathdb.vdi.lib.json.JSON
import java.io.InputStream
import vdi.lib.logging.markedLogger
import vdi.lib.s3.files.*
import vdi.lib.s3.paths.S3DatasetPathFactory

internal class DatasetDirectoryImpl(
  override val ownerID: UserID,
  override val datasetID: DatasetID,
  private val bucket: S3Bucket,
  private val pathFactory: S3DatasetPathFactory,
) : DatasetDirectory {
  private val log = markedLogger<DatasetDirectory>(ownerID, datasetID)

  override fun exists(): Boolean =
    bucket.objects.listSubPaths(pathFactory.datasetDir()).count > 0


  override fun deleteMetaFile() =
    bucket.objects.delete(pathFactory.datasetMetaFile())

  override fun getMetaFile() =
    DatasetMetaFileImpl(bucket, pathFactory.datasetMetaFile())

  override fun hasMetaFile() =
    pathFactory.datasetMetaFile() in bucket.objects

  override fun putMetaFile(meta: VDIDatasetMeta) {
    bucket.objects.put(pathFactory.datasetMetaFile()) {
      contentType = "application/json"
      stream = JSON.writeValueAsBytes(meta).inputStream()
    }
  }


  override fun deleteManifestFile() =
    bucket.objects.delete(pathFactory.datasetManifestFile())

  override fun getManifestFile() =
    DatasetManifestFileImpl(bucket, pathFactory.datasetManifestFile())

  override fun hasManifestFile() =
    pathFactory.datasetManifestFile() in bucket.objects

  override fun putManifestFile(manifest: VDIDatasetManifest) {
    bucket.objects.put(pathFactory.datasetManifestFile()) {
      contentType = "application/json"
      stream = JSON.writeValueAsBytes(manifest).inputStream()
    }
  }


  override fun deleteDeleteFlag() =
    bucket.objects.delete(pathFactory.datasetDeleteFlagFile())

  override fun getDeleteFlag() =
    DatasetDeleteFlagFileImpl(bucket, pathFactory.datasetDeleteFlagFile())

  override fun hasDeleteFlag() =
    pathFactory.datasetDeleteFlagFile() in bucket.objects

  override fun putDeleteFlag() {
    bucket.objects.touch(pathFactory.datasetDeleteFlagFile()) {
      contentType = "text/plain"
    }
  }


  override fun deleteUploadFile() =
    bucket.objects.delete(pathFactory.datasetUploadZip())

  override fun getUploadFile() =
    DatasetRawUploadFileImpl(bucket, pathFactory.datasetUploadZip())

  override fun hasUploadFile() =
    pathFactory.datasetUploadZip() in bucket.objects

  override fun putUploadFile(fn: () -> InputStream): Unit =
    fn().use { bucket.objects.put(pathFactory.datasetUploadZip()) {
      contentType = "application/zip"
      stream = it.buffered()
    } }


  override fun deleteImportReadyFile() =
    bucket.objects.delete(pathFactory.datasetImportReadyZip())

  override fun getImportReadyFile() =
    DatasetImportableFileImpl(bucket, pathFactory.datasetImportReadyZip())

  override fun hasImportReadyFile() =
    pathFactory.datasetImportReadyZip() in bucket.objects

  override fun putImportReadyFile(fn: () -> InputStream): Unit =
    fn().use { bucket.objects.put(pathFactory.datasetImportReadyZip()) {
      contentType = "application/zip"
      stream = it.buffered()
    } }


  override fun deleteInstallReadyFile() =
    bucket.objects.delete(pathFactory.datasetInstallReadyZip())

  override fun getInstallReadyFile() =
    DatasetInstallableFileImpl(bucket, pathFactory.datasetInstallReadyZip())

  override fun hasInstallReadyFile() =
    pathFactory.datasetInstallReadyZip() in bucket.objects

  override fun putInstallReadyFile(fn: () -> InputStream): Unit =
    fn().use { bucket.objects.put(pathFactory.datasetInstallReadyZip()) {
      contentType = "application/zip"
      stream = it.buffered()
    } }


  override fun deleteRevisedFlag() =
    bucket.objects.delete(pathFactory.datasetRevisedFlagFile())

  override fun getRevisedFlag() =
    DatasetRevisionFlagFileImpl(bucket, pathFactory.datasetRevisedFlagFile())

  override fun hasRevisedFlag() =
    pathFactory.datasetRevisedFlagFile() in bucket.objects

  override fun putRevisedFlag() {
    bucket.objects.touch(pathFactory.datasetRevisedFlagFile()) {
      contentType = "text/plain"
    }
  }


  override fun getShares(): Map<UserID, DatasetShare> {
    log.debug("looking up shares")

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
    putShare(
      recipientID,
      VDIDatasetShareOffer(VDIShareOfferAction.Grant),
      VDIDatasetShareReceipt(VDIShareReceiptAction.Accept)
    )
  }

  override fun putShare(recipientID: UserID, offer: VDIDatasetShareOffer, receipt: VDIDatasetShareReceipt) {
    log.debug("putting a new share for user {} with offer action {} and receipt action {} ", recipientID, offer.action, receipt.action)

    bucket.objects.put(pathFactory.datasetShareOfferFile(recipientID)) {
      contentType = "application/json"
      stream = JSON.writeValueAsBytes(offer).inputStream()
    }
    bucket.objects.put(pathFactory.datasetShareReceiptFile(recipientID)) {
      contentType = "application/json"
      stream = JSON.writeValueAsBytes(receipt).inputStream()
    }
  }
}
