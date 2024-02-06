package org.veupathdb.vdi.lib.s3.datasets

import org.veupathdb.lib.s3.s34k.objects.S3Object
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toUserIDOrNull
import org.veupathdb.vdi.lib.common.model.VDIDatasetManifest
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.s3.datasets.exception.MalformedDatasetException
import org.veupathdb.vdi.lib.s3.datasets.files.*
import org.veupathdb.vdi.lib.s3.datasets.paths.S3DatasetPathFactory
import org.veupathdb.vdi.lib.s3.datasets.paths.S3Paths
import java.io.InputStream

/**
 * Implementation of a DatasetDirectory in which all objects are already known
 * and metadata is in memory at the time of construction. This allows perusing a
 * known dataset's files without making remote calls to the underlying data
 * store.
 */
internal class EagerlyLoadedDatasetDirectory(
  objects: List<S3Object>,
  override val ownerID: UserID,
  override val datasetID: DatasetID,
  private val pathFactory: S3DatasetPathFactory
) : DatasetDirectory {

  private var metaFile: DatasetMetaFile? = null
  private var manifest: DatasetManifestFile? = null
  private var uploadFile: DatasetRawUploadFile? = null
  private var importableFile: DatasetImportableFile? = null
  private var installableFile: DatasetInstallableFile? = null
  private var deleteFlag: DatasetDeleteFlagFile? = null
  private val shares: Map<UserID, DatasetShare>

  init {
    val shareRefs = HashMap<UserID, ShareRef>(4)

    objects.forEach {
      val subPath = it.path.cutIDPrefix()

      if (subPath.startsWith(S3Paths.SharesDirName)) {
        val recipient = subPath.getRecipientID()

        if (recipient != null) {
          val ok = shareRefs.computeIfAbsent(recipient) { ShareRef(recipient) }
            .set(it)

          if (ok)
            return@forEach
        }
      }

      when (subPath) {
        S3Paths.MetadataFileName -> metaFile = DatasetMetaFileImpl(it)
        S3Paths.ManifestFileName -> manifest = DatasetManifestFileImpl(it)
        S3Paths.RawUploadZipName -> uploadFile = DatasetRawUploadFileImpl(it)
        S3Paths.ImportReadyZipName -> importableFile = DatasetImportableFileImpl(it)
        S3Paths.InstallReadyZipName -> installableFile = DatasetInstallableFileImpl(it)
        S3Paths.DeleteFlagFileName -> deleteFlag = DatasetDeleteFlagFileImpl(it)
        else -> throw MalformedDatasetException("Unrecognized file path in S3: " + it.path)
      }
    }

    shares = shareRefs.mapValues { (_, ref) -> ref.toDatasetShare(pathFactory) }
  }

  override fun exists(): Boolean = true // Eagerly loaded dataset directory must exist by definition of being constructed.

  override fun hasMeta() = metaFile != null

  override fun getMeta() = metaFile ?: DatasetMetaFileImpl(pathFactory.datasetMetaFile())

  override fun putMeta(meta: VDIDatasetMeta) = throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasManifest() = manifest != null

  override fun getManifest() = manifest ?: DatasetManifestFileImpl(pathFactory.datasetManifestFile())

  override fun putManifest(manifest: VDIDatasetManifest) = throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasDeleteFlag() = deleteFlag != null

  override fun getDeleteFlag() = deleteFlag ?: DatasetDeleteFlagFileImpl(pathFactory.datasetDeleteFlagFile())

  override fun putDeleteFlag() = throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasUploadFile() = uploadFile != null

  override fun getUploadFile() = uploadFile ?: DatasetRawUploadFileImpl(pathFactory.datasetUploadZip())

  override fun putUploadFile(fn: () -> InputStream) = throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasImportReadyFile() = importableFile != null

  override fun getImportReadyFile() = importableFile ?: DatasetImportableFileImpl(pathFactory.datasetImportReadyZip())

  override fun putImportReadyFile(fn: () -> InputStream) = throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasInstallReadyFile() = installableFile != null

  override fun getInstallReadyFile() = installableFile ?: DatasetInstallableFileImpl(pathFactory.datasetInstallReadyZip())

  override fun putInstallReadyFile(fn: () -> InputStream) = throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun getShares(): Map<UserID, DatasetShare> = shares

  override fun putShare(recipientID: UserID) = throw UnsupportedOperationException("${javaClass.name} is read-only")
}

private data class ShareRef(
  val recipientID: UserID,
  var offer: DatasetShareOfferFile? = null,
  var receipt: DatasetShareReceiptFile? = null,
) {
  fun set(unknown: S3Object): Boolean {
    when (unknown.baseName) {
      S3Paths.ShareOfferFileName -> offer = DatasetShareOfferFileImpl(unknown)
      S3Paths.ShareReceiptFileName -> receipt = DatasetShareReceiptFileImpl(unknown)
      else -> return false
    }

    return true
  }

  fun toDatasetShare(pathFactory: S3DatasetPathFactory): DatasetShare {
    return DatasetShareImpl(
      recipientID,
      offer ?: DatasetShareOfferFileImpl(pathFactory.datasetShareOfferFile(recipientID)),
      receipt ?: DatasetShareReceiptFileImpl(pathFactory.datasetShareReceiptFile(recipientID))
    )
  }
}

private fun String.cutIDPrefix() =
  when (val i = indexOf('/', indexOf('/')+1)) {
    -1   -> ""
    else -> substring(i+1)
  }

private fun String.getRecipientID() = substring(lastIndexOf('/')+1).toUserIDOrNull()