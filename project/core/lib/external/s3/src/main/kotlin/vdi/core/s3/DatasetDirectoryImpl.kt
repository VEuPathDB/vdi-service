package vdi.core.s3

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import vdi.model.meta.*
import vdi.model.meta.DatasetManifest
import vdi.json.JSON
import java.io.InputStream
import vdi.core.s3.files.data.ImportReadyFile
import vdi.core.s3.files.data.InstallReadyFile
import vdi.core.s3.files.data.RawUploadFile
import vdi.core.s3.files.docs.DocumentFile
import vdi.core.s3.files.flags.DeleteFlagFile
import vdi.core.s3.files.flags.RevisedFlagFile
import vdi.core.s3.files.maps.MappingFile
import vdi.core.s3.files.meta.ManifestFile
import vdi.core.s3.files.meta.MetaFile
import vdi.core.s3.files.shares.ShareOffer
import vdi.core.s3.files.shares.ShareReceipt
import vdi.logging.markedLogger
import vdi.core.s3.paths.S3DatasetPathFactory

internal class DatasetDirectoryImpl(
  override val ownerID: UserID,
  override val datasetID: DatasetID,
  private val bucket: S3Bucket,
  private val pathFactory: S3DatasetPathFactory,
): DatasetDirectory {
  private val log = markedLogger<DatasetDirectory>(ownerID, datasetID)

  override fun exists(): Boolean =
    bucket.objects.listSubPaths(pathFactory.datasetDir()).count > 0


  private val lazyMetadata by lazy { MetaFile(pathFactory.datasetMetaFile(), bucket.objects) }

  override fun deleteMetaFile() = lazyMetadata.delete()
  override fun getMetaFile() = lazyMetadata
  override fun hasMetaFile() = lazyMetadata.exists()
  override fun putMetaFile(meta: DatasetMetadata) = lazyMetadata.put(meta)


  private val lazyManifest by lazy { ManifestFile(pathFactory.datasetManifestFile(), bucket.objects) }

  override fun deleteManifestFile() = lazyManifest.delete()
  override fun getManifestFile() = lazyManifest
  override fun hasManifestFile() = lazyManifest.exists()
  override fun putManifestFile(manifest: DatasetManifest) = this.lazyManifest.put(manifest)


  private val lazyDeleteFlag by lazy { DeleteFlagFile(pathFactory.datasetDeleteFlagFile(), bucket.objects) }

  override fun deleteDeleteFlag() = lazyDeleteFlag.delete()
  override fun getDeleteFlag() = lazyDeleteFlag
  override fun hasDeleteFlag() = lazyDeleteFlag.exists()
  override fun putDeleteFlag() = lazyDeleteFlag.create()


  private val lazyUpload by lazy { RawUploadFile(pathFactory.datasetUploadZip(), bucket.objects) }

  override fun deleteUploadFile() = lazyUpload.delete()
  override fun getUploadFile() = lazyUpload
  override fun hasUploadFile() = lazyUpload.exists()
  override fun putUploadFile(fn: () -> InputStream) = fn().use { lazyUpload.writeContents(it) }


  private val lazyImportReady by lazy { ImportReadyFile(pathFactory.datasetImportReadyZip(), bucket.objects) }

  override fun deleteImportReadyFile() = lazyImportReady.delete()
  override fun getImportReadyFile() = lazyImportReady
  override fun hasImportReadyFile() = lazyImportReady.exists()
  override fun putImportReadyFile(fn: () -> InputStream) = fn().use { lazyImportReady.writeContents(it) }


  private val lazyInstallReady by lazy { InstallReadyFile(pathFactory.datasetInstallReadyZip(), bucket.objects) }

  override fun deleteInstallReadyFile() = lazyInstallReady.delete()
  override fun getInstallReadyFile() = lazyInstallReady
  override fun hasInstallReadyFile() = lazyInstallReady.exists()
  override fun putInstallReadyFile(fn: () -> InputStream) = fn().use { lazyInstallReady.writeContents(it) }


  private val lazyRevisedFlag by lazy { RevisedFlagFile(pathFactory.datasetRevisedFlagFile(), bucket.objects) }

  override fun deleteRevisedFlag() = lazyRevisedFlag.delete()
  override fun getRevisedFlag() = lazyRevisedFlag
  override fun hasRevisedFlag() = lazyRevisedFlag.exists()
  override fun putRevisedFlag() = lazyRevisedFlag.create()

  override fun getMappingFiles(): Sequence<MappingFile> {
    log.debug("looking up mapping files")

    TODO("Not yet implemented")
  }

  override fun getMappingFile(name: String): MappingFile? {
    TODO("Not yet implemented")
  }

  override fun putMappingFile(name: String, fn: () -> InputStream) {
    TODO("Not yet implemented")
  }

  override fun deleteMappingFile(name: String) {
    TODO("Not yet implemented")
  }

  override fun getDocumentFiles(): Sequence<DocumentFile> {
    TODO("Not yet implemented")
  }

  override fun getDocumentFile(name: String): DocumentFile? {
    TODO("Not yet implemented")
  }

  override fun putDocumentFile(name: String, fn: () -> InputStream) {
    TODO("Not yet implemented")
  }

  override fun deleteDocumentFile(name: String) {
    TODO("Not yet implemented")
  }

  override fun getShares(): Map<UserID, Pair<ShareOffer, ShareReceipt>> {
    log.debug("looking up shares")

    val pathPrefix = pathFactory.datasetSharesDir()
    val subPaths   = bucket.objects.listSubPaths(pathPrefix).commonPrefixes()
    val retValue   = HashMap<UserID, Pair<ShareOffer, ShareReceipt>>(subPaths.size)

    subPaths.forEach {
      val recipientID = it.substring(pathPrefix.length)
        .trim('/')
        .toUserIDOrNull()
        ?: throw IllegalStateException("invalid user ID")

      retValue[recipientID] = Pair(
        ShareOffer(recipientID, pathFactory.datasetShareOfferFile(recipientID), bucket.objects),
        ShareReceipt(recipientID, pathFactory.datasetShareReceiptFile(recipientID), bucket.objects),
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
