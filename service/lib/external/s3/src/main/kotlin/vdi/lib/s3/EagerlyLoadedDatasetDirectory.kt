package vdi.lib.s3

import org.veupathdb.lib.s3.s34k.objects.S3Object
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toUserIDOrNull
import org.veupathdb.vdi.lib.common.model.VDIDatasetManifest
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDIDatasetShareOffer
import org.veupathdb.vdi.lib.common.model.VDIDatasetShareReceipt
import java.io.InputStream
import vdi.lib.s3.exception.MalformedDatasetException
import vdi.lib.s3.files.*
import vdi.lib.s3.paths.S3DatasetPathFactory
import vdi.lib.s3.paths.S3File
import vdi.lib.s3.paths.S3File.Companion.resembles

/**
 * Implementation of a DatasetDirectory in which all objects are already known
 * and metadata is in memory at the time of construction. This allows perusing a
 * known dataset's files without making remote calls to the underlying data
 * store.
 */
internal class EagerlyLoadedDatasetDirectory(
  override val ownerID: UserID,
  override val datasetID: DatasetID,

  private val metaFile: DatasetMetaFile?,
  private val manifest: DatasetManifestFile?,
  private val uploadFile: DatasetRawUploadFile?,
  private val importableFile: DatasetImportableFile?,
  private val installableFile: DatasetInstallableFile?,
  private val deleteFlag: DatasetDeleteFlagFile?,
  private val revisedFlag: DatasetRevisionFlagFile?,
  private val shares: Map<UserID, DatasetShare>,
  private val pathFactory: S3DatasetPathFactory
) : DatasetDirectory {
  companion object {
    // throws
    @JvmStatic
    fun build(
      objects: List<S3Object>,
      ownerID: UserID,
      datasetID: DatasetID,
      pathFactory: S3DatasetPathFactory,
    ): EagerlyLoadedDatasetDirectory {
      var metaFile: DatasetMetaFile? = null
      var manifest: DatasetManifestFile? = null
      var uploadFile: DatasetRawUploadFile? = null
      var importableFile: DatasetImportableFile? = null
      var installableFile: DatasetInstallableFile? = null
      var deleteFlag: DatasetDeleteFlagFile? = null
      var revisedFlag: DatasetRevisionFlagFile? = null

      val shareRefs = HashMap<UserID, ShareRef>(4)

      objects.forEach {
        val subPath = it.path.trimIDPrefix()

        subPath.resembles(S3File.SharesDir) {
          subPath.splitSharePath()?.also { (recipient, file) ->
            val ref = shareRefs.computeIfAbsent(recipient) { ShareRef(recipient) }

            file.resembles(S3File.ShareOffer) { ref.offer = it }
            || file.resembles(S3File.ShareReceipt) { ref.receipt = it }
            || throw MalformedDatasetException("Unrecognized file path in S3: " + it.path)
          }
        }
        || subPath.resembles(S3File.Metadata) { metaFile = DatasetMetaFileImpl(it) }
        || subPath.resembles(S3File.Manifest) { manifest = DatasetManifestFileImpl(it) }
        || subPath.resembles(S3File.RawUploadZip) { uploadFile = DatasetRawUploadFileImpl(it) }
        || subPath.resembles(S3File.ImportReadyZip) { importableFile = DatasetImportableFileImpl(it) }
        || subPath.resembles(S3File.InstallReadyZip) { installableFile = DatasetInstallableFileImpl(it) }
        || subPath.resembles(S3File.DeleteFlag) { deleteFlag = DatasetDeleteFlagFileImpl(it) }
        || subPath.resembles(S3File.RevisionFlag) { revisedFlag = DatasetRevisionFlagFileImpl(it) }
        || throw MalformedDatasetException("Unrecognized file path in S3: " + it.path)
      }

      return EagerlyLoadedDatasetDirectory(
        ownerID         = ownerID,
        datasetID       = datasetID,
        metaFile        = metaFile!!,
        manifest        = manifest,
        uploadFile      = uploadFile,
        importableFile  = importableFile,
        installableFile = installableFile,
        deleteFlag      = deleteFlag,
        revisedFlag     = revisedFlag,
        shares          = shareRefs.mapValues { (_, ref) -> ref.toDatasetShare(pathFactory) },
        pathFactory     = pathFactory,
      )
    }

    private data class ShareRef(
      val recipientID: UserID,
      var offer: S3Object? = null,
      var receipt: S3Object? = null,
    ) {
      fun toDatasetShare(pathFactory: S3DatasetPathFactory) =
        DatasetShareImpl(
          recipientID,
          offer?.let(::DatasetShareOfferFileImpl)
            ?: DatasetShareOfferFileImpl(pathFactory.datasetShareOfferFile(recipientID)),
          receipt?.let(::DatasetShareReceiptFileImpl)
            ?: DatasetShareReceiptFileImpl(pathFactory.datasetShareReceiptFile(recipientID))
        )
    }

    /**
     * Removes the "{user-id}/{dataset-id}/" prefix from the given path string.
     */
    private fun String.trimIDPrefix() =
      when (val i = indexOf('/', indexOf('/')+1)) {
        -1   -> ""
        else -> substring(i+1)
      }

    /**
     * Attempts to split the given share object path into the component recipient
     * user id and share object name.
     *
     * Expected object path format: `share/{user-id}/{file-name}`
     *
     * If the path cannot be parsed as the expected form, this method will return
     * `null`.
     */
    private fun String.splitSharePath(): Pair<UserID, String>? {
      val a = indexOf('/')
      if (a == -1)
        return null

      val b = indexOf('/', a + 1)
      if (b == -1)
        return null

      val recipientID = substring(a + 1, b).toUserIDOrNull()
        ?: return null

      return recipientID to substring(b + 1)
    }
  }

  // Eagerly loaded dataset directory must exist by definition of being constructed.
  override fun exists(): Boolean = true

  override fun hasMetaFile() =
    metaFile != null

  override fun getMetaFile() =
    metaFile ?: DatasetMetaFileImpl(pathFactory.datasetMetaFile())

  override fun putMetaFile(meta: VDIDatasetMeta) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteMetaFile() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun hasManifestFile() =
    manifest != null

  override fun getManifestFile() =
    manifest ?: DatasetManifestFileImpl(pathFactory.datasetManifestFile())

  override fun putManifestFile(manifest: VDIDatasetManifest) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteManifestFile() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun hasDeleteFlag() =
    deleteFlag != null

  override fun getDeleteFlag() =
    deleteFlag ?: DatasetDeleteFlagFileImpl(pathFactory.datasetDeleteFlagFile())

  override fun putDeleteFlag() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteDeleteFlag() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun hasRevisedFlag() =
    revisedFlag != null

  override fun getRevisedFlag() =
    revisedFlag ?: DatasetRevisionFlagFileImpl(pathFactory.datasetRevisedFlagFile())

  override fun putRevisedFlag() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteRevisedFlag() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun hasUploadFile() =
    uploadFile != null

  override fun getUploadFile() =
    uploadFile ?: DatasetRawUploadFileImpl(pathFactory.datasetUploadZip())

  override fun putUploadFile(fn: () -> InputStream) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteUploadFile() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun hasImportReadyFile() =
    importableFile != null

  override fun getImportReadyFile() =
    importableFile ?: DatasetImportableFileImpl(pathFactory.datasetImportReadyZip())

  override fun putImportReadyFile(fn: () -> InputStream) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteImportReadyFile() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun hasInstallReadyFile() =
    installableFile != null

  override fun getInstallReadyFile() =
    installableFile ?: DatasetInstallableFileImpl(pathFactory.datasetInstallReadyZip())

  override fun putInstallReadyFile(fn: () -> InputStream) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteInstallReadyFile() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun getShares(): Map<UserID, DatasetShare> = shares

  override fun putShare(recipientID: UserID) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun putShare(recipientID: UserID, offer: VDIDatasetShareOffer, receipt: VDIDatasetShareReceipt) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun toString() = "EagerDatasetDir($ownerID/$datasetID)"
}
