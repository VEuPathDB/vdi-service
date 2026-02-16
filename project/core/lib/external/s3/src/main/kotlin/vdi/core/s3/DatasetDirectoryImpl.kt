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
import vdi.core.s3.files.maps.DataPropertiesFile
import vdi.core.s3.files.meta.ManifestFile
import vdi.core.s3.files.meta.MetaFile
import vdi.core.s3.files.meta.UploadErrorFile
import vdi.core.s3.files.shares.ShareOffer
import vdi.core.s3.files.shares.ShareReceipt
import vdi.logging.markedLogger
import vdi.core.s3.paths.S3DatasetPathFactory
import vdi.model.misc.UploadErrorReport

internal class DatasetDirectoryImpl(
  override val ownerID: UserID,
  override val datasetID: DatasetID,
  private val bucket: S3Bucket,
  private val pathFactory: S3DatasetPathFactory,
): DatasetDirectory {
  private val log = markedLogger<DatasetDirectory>(ownerID = ownerID, datasetID = datasetID)

  override fun exists(): Boolean =
    bucket.objects.listSubPaths(pathFactory.datasetDir()).count > 0


  private val lazyMetadata by lazy { MetaFile(pathFactory.metadataFile(), bucket.objects) }

  override fun deleteMetaFile() = lazyMetadata.delete()
  override fun getMetaFile() = lazyMetadata
  override fun hasMetaFile() = lazyMetadata.exists()
  override fun putMetaFile(meta: DatasetMetadata) = lazyMetadata.put(meta)


  private val lazyManifest by lazy { ManifestFile(pathFactory.manifestFile(), bucket.objects) }

  override fun deleteManifestFile() = lazyManifest.delete()
  override fun getManifestFile() = lazyManifest
  override fun hasManifestFile() = lazyManifest.exists()
  override fun putManifestFile(manifest: DatasetManifest) = this.lazyManifest.put(manifest)


  private val lazyDeleteFlag by lazy { DeleteFlagFile(pathFactory.deleteFlagFile(), bucket.objects) }

  override fun deleteDeleteFlag() = lazyDeleteFlag.delete()
  override fun getDeleteFlag() = lazyDeleteFlag
  override fun hasDeleteFlag() = lazyDeleteFlag.exists()
  override fun putDeleteFlag() = lazyDeleteFlag.create()


  private val lazyUpload by lazy { RawUploadFile(pathFactory.uploadZip(), bucket.objects) }

  override fun deleteUploadFile() = lazyUpload.delete()
  override fun getUploadFile() = lazyUpload
  override fun hasUploadFile() = lazyUpload.exists()
  override fun putUploadFile(provider: () -> InputStream) = lazyUpload.put(provider)


  private val lazyImportReady by lazy { ImportReadyFile(pathFactory.importReadyZip(), bucket.objects) }

  override fun deleteImportReadyFile() = lazyImportReady.delete()
  override fun getImportReadyFile() = lazyImportReady
  override fun hasImportReadyFile() = lazyImportReady.exists()
  override fun putImportReadyFile(provider: () -> InputStream) = lazyImportReady.put(provider)


  private val lazyInstallReady by lazy { InstallReadyFile(pathFactory.installReadyZip(), bucket.objects) }

  override fun deleteInstallReadyFile() = lazyInstallReady.delete()
  override fun getInstallReadyFile() = lazyInstallReady
  override fun hasInstallReadyFile() = lazyInstallReady.exists()
  override fun putInstallReadyFile(provider: () -> InputStream) = lazyInstallReady.put(provider)


  private val lazyRevisedFlag by lazy { RevisedFlagFile(pathFactory.revisedFlagFile(), bucket.objects) }

  override fun deleteRevisedFlag() = lazyRevisedFlag.delete()
  override fun getRevisedFlag() = lazyRevisedFlag
  override fun hasRevisedFlag() = lazyRevisedFlag.exists()
  override fun putRevisedFlag() = lazyRevisedFlag.create()

  private val lazyErrorReport by lazy { UploadErrorFile(pathFactory.uploadErrorFile(), bucket.objects) }

  override fun deleteUploadErrorFile() = lazyErrorReport.delete()
  override fun getUploadErrorFile() = lazyErrorReport
  override fun hasUploadErrorFile() = lazyErrorReport.exists()
  override fun putUploadErrorFile(report: UploadErrorReport) = lazyErrorReport.put(report)

  override fun getDataPropertiesFiles(): Sequence<DataPropertiesFile> {
    log.debug("looking up mapping files")

    return bucket.objects.listSubPaths(pathFactory.mappingsDir())
      .contents()
      .asSequence()
      .map { MappingFile(it.path, bucket.objects) }
  }

  override fun getDataPropertiesFile(name: String): DataPropertiesFile =
    MappingFile(pathFactory.mappingFile(name), bucket.objects)

  override fun putDataPropertiesFile(name: String, contentType: String, provider: () -> InputStream) {
    provider().use { bucket.objects.put(pathFactory.mappingFile(name)) {
      this.contentType = contentType
      this.stream = it
    } }
  }

  override fun deleteDataPropertiesFile(name: String) =
    bucket.objects.delete(pathFactory.mappingFile(name))

  override fun getDocumentFiles(): Sequence<DocumentFile> {
    log.debug("looking up document files")

    return bucket.objects.listSubPaths(pathFactory.documentsDir())
      .contents()
      .asSequence()
      .map { DocumentFile(it.path, bucket.objects) }
  }

  override fun getDocumentFile(name: String): DocumentFile =
    DocumentFile(pathFactory.documentFile(name), bucket.objects)

  override fun putDocumentFile(name: String, contentType: String, provider: () -> InputStream) {
    provider().use { bucket.objects.put(pathFactory.documentFile(name)) {
      this.contentType = contentType
      this.stream = it
    } }
  }

  override fun deleteDocumentFile(name: String) =
    bucket.objects.delete(pathFactory.documentFile(name))

  override fun getShares(): Map<UserID, Pair<ShareOffer, ShareReceipt>> {
    log.debug("looking up shares")

    val pathPrefix = pathFactory.sharesDir()
    val subPaths   = bucket.objects.listSubPaths(pathPrefix).commonPrefixes()
    val retValue   = HashMap<UserID, Pair<ShareOffer, ShareReceipt>>(subPaths.size)

    subPaths.forEach {
      val recipientID = it.substring(pathPrefix.length)
        .trim('/')
        .toUserIDOrNull()
        ?: throw IllegalStateException("invalid user ID")

      retValue[recipientID] = Pair(
        ShareOffer(recipientID, pathFactory.shareOfferFile(recipientID), bucket.objects),
        ShareReceipt(recipientID, pathFactory.shareReceiptFile(recipientID), bucket.objects),
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

    bucket.objects.put(pathFactory.shareOfferFile(recipientID)) {
      contentType = "application/json"
      stream = JSON.writeValueAsBytes(offer).inputStream()
    }
    bucket.objects.put(pathFactory.shareReceiptFile(recipientID)) {
      contentType = "application/json"
      stream = JSON.writeValueAsBytes(receipt).inputStream()
    }
  }
}
