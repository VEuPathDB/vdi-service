package vdi.lib.s3

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import vdi.model.data.*
import vdi.json.JSON
import java.io.InputStream
import vdi.core.logging.markedLogger
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


  private val metadata by lazy { DatasetMetaFile.createMetadata(pathFactory.datasetMetaFile(), bucket) }

  override fun deleteMetaFile() = metadata.delete()
  override fun getMetaFile() = metadata
  override fun hasMetaFile() = metadata.exists()
  override fun putMetaFile(meta: DatasetMetadata) = metadata.put(meta)


  private val manifest by lazy { DatasetMetaFile.createManifest(pathFactory.datasetManifestFile(), bucket) }

  override fun deleteManifestFile() = manifest.delete()
  override fun getManifestFile() = manifest
  override fun hasManifestFile() = manifest.exists()
  override fun putManifestFile(manifest: DatasetManifest) = this.manifest.put(manifest)


  private val deleteFlag by lazy { DatasetFlagFile.create(pathFactory.datasetDeleteFlagFile(), bucket) }

  override fun deleteDeleteFlag() = deleteFlag.delete()
  override fun getDeleteFlag() = deleteFlag
  override fun hasDeleteFlag() = deleteFlag.exists()
  override fun putDeleteFlag() = deleteFlag.touch()


  private val rawUpload by lazy { DatasetDataFile.create(pathFactory.datasetUploadZip(), bucket) }

  override fun deleteUploadFile() = rawUpload.delete()
  override fun getUploadFile() = rawUpload
  override fun hasUploadFile() = rawUpload.exists()
  override fun putUploadFile(fn: () -> InputStream) = fn().use { rawUpload.writeContents(it) }


  private val importReady by lazy { DatasetDataFile.create(pathFactory.datasetImportReadyZip(), bucket) }

  override fun deleteImportReadyFile() = importReady.delete()
  override fun getImportReadyFile() = importReady
  override fun hasImportReadyFile() = importReady.exists()
  override fun putImportReadyFile(fn: () -> InputStream) = fn().use { importReady.writeContents(it) }


  private val installReady by lazy { DatasetDataFile.create(pathFactory.datasetInstallReadyZip(), bucket) }

  override fun deleteInstallReadyFile() = installReady.delete()
  override fun getInstallReadyFile() = installReady
  override fun hasInstallReadyFile() = installReady.exists()
  override fun putInstallReadyFile(fn: () -> InputStream) = fn().use { installReady.writeContents(it) }


  private val revisedFlag by lazy { DatasetFlagFile.create(pathFactory.datasetRevisedFlagFile(), bucket) }

  override fun deleteRevisedFlag() = revisedFlag.delete()
  override fun getRevisedFlag() = revisedFlag
  override fun hasRevisedFlag() = revisedFlag.exists()
  override fun putRevisedFlag() = revisedFlag.touch()


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
