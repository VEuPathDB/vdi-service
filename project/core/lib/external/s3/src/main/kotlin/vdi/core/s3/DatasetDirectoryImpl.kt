package vdi.core.s3

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import vdi.model.data.*
import vdi.json.JSON
import java.io.InputStream
import vdi.logging.markedLogger
import vdi.core.s3.files.*
import vdi.core.s3.paths.S3DatasetPathFactory

internal class DatasetDirectoryImpl(
  override val ownerID: UserID,
  override val datasetID: DatasetID,
  private val bucket: S3Bucket,
  private val pathFactory: S3DatasetPathFactory,
) : DatasetDirectory {
  private val log = markedLogger<DatasetDirectory>(ownerID, datasetID)

  override fun exists(): Boolean =
    bucket.objects.listSubPaths(pathFactory.datasetDir()).count > 0


  private val lazyMetadata by lazy { DatasetMetaFile.createMetadata(pathFactory.datasetMetaFile(), bucket) }

  override fun deleteMetaFile() = lazyMetadata.delete()
  override fun getMetaFile() = lazyMetadata
  override fun hasMetaFile() = lazyMetadata.exists()
  override fun putMetaFile(meta: DatasetMetadata) = lazyMetadata.put(meta)


  private val lazyManifest by lazy { DatasetMetaFile.createManifest(pathFactory.datasetManifestFile(), bucket) }

  override fun deleteManifestFile() = lazyManifest.delete()
  override fun getManifestFile() = lazyManifest
  override fun hasManifestFile() = lazyManifest.exists()
  override fun putManifestFile(manifest: DatasetManifest) = this.lazyManifest.put(manifest)


  private val lazyDeleteFlag by lazy { DatasetFlagFile.create(pathFactory.datasetDeleteFlagFile(), bucket) }

  override fun deleteDeleteFlag() = lazyDeleteFlag.delete()
  override fun getDeleteFlag() = lazyDeleteFlag
  override fun hasDeleteFlag() = lazyDeleteFlag.exists()
  override fun putDeleteFlag() = lazyDeleteFlag.touch()


  private val lazyUpload by lazy { DatasetDataFile.create(pathFactory.datasetUploadZip(), bucket) }

  override fun deleteUploadFile() = lazyUpload.delete()
  override fun getUploadFile() = lazyUpload
  override fun hasUploadFile() = lazyUpload.exists()
  override fun putUploadFile(fn: () -> InputStream) = fn().use { lazyUpload.writeContents(it) }


  private val lazyImportReady by lazy { DatasetDataFile.create(pathFactory.datasetImportReadyZip(), bucket) }

  override fun deleteImportReadyFile() = lazyImportReady.delete()
  override fun getImportReadyFile() = lazyImportReady
  override fun hasImportReadyFile() = lazyImportReady.exists()
  override fun putImportReadyFile(fn: () -> InputStream) = fn().use { lazyImportReady.writeContents(it) }


  private val lazyInstallReady by lazy { DatasetDataFile.create(pathFactory.datasetInstallReadyZip(), bucket) }

  override fun deleteInstallReadyFile() = lazyInstallReady.delete()
  override fun getInstallReadyFile() = lazyInstallReady
  override fun hasInstallReadyFile() = lazyInstallReady.exists()
  override fun putInstallReadyFile(fn: () -> InputStream) = fn().use { lazyInstallReady.writeContents(it) }


  private val lazyRevisedFlag by lazy { DatasetFlagFile.create(pathFactory.datasetRevisedFlagFile(), bucket) }

  override fun deleteRevisedFlag() = lazyRevisedFlag.delete()
  override fun getRevisedFlag() = lazyRevisedFlag
  override fun hasRevisedFlag() = lazyRevisedFlag.exists()
  override fun putRevisedFlag() = lazyRevisedFlag.touch()


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

      retValue[recipientID] = DatasetShare.create(
        recipientID,
        DatasetShareFile.createOffer(pathFactory.datasetShareOfferFile(recipientID), recipientID, bucket),
        DatasetShareFile.createReceipt(pathFactory.datasetShareReceiptFile(recipientID), recipientID, bucket),
      )
    }

    return retValue
  }

  override fun putShare(recipientID: UserID) {
    putShare(
      recipientID,
      DatasetShareOffer(DatasetShareOffer.Action.Grant),
      DatasetShareReceipt(DatasetShareReceipt.Action.Accept)
    )
  }

  override fun putShare(recipientID: UserID, offer: DatasetShareOffer, receipt: DatasetShareReceipt) {
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
