package vdi.core.s3

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.model.data.DatasetManifest
import vdi.model.data.DatasetMetadata
import vdi.model.data.DatasetShareOffer
import vdi.model.data.DatasetShareReceipt
import java.io.InputStream
import vdi.core.s3.exception.MalformedDatasetException
import vdi.core.s3.files.*
import vdi.core.s3.paths.*
import vdi.core.s3.paths.S3DatasetPathFactory

/**
 * Implementation of a DatasetDirectory in which all objects are already known
 * and metadata is in memory at the time of construction. This allows perusing a
 * known dataset's files without making remote calls to the underlying data
 * store.
 */
internal class MaterializedDatasetDirectory(
  override val ownerID: UserID,
  override val datasetID: DatasetID,

  private val bucket: S3Bucket,
  private val pathFactory: S3DatasetPathFactory,

  private val metaFile: DatasetMetaFile<DatasetMetadata>?,
  private val manifest: DatasetMetaFile<DatasetManifest>?,
  private val uploadFile: DatasetDataFile?,
  private val importableFile: DatasetDataFile?,
  private val installableFile: DatasetDataFile?,
  private val deleteFlag: DatasetFlagFile?,
  private val revisedFlag: DatasetFlagFile?,
  private val shares: Map<UserID, DatasetShare>,
): DatasetDirectory {
  companion object {
    // throws
    @JvmStatic
    fun build(
      bucket: S3Bucket,
      objects: List<S3Object>,
      ownerID: UserID,
      datasetID: DatasetID,
      pathFactory: S3DatasetPathFactory,
    ): MaterializedDatasetDirectory {
      var metaFile: DatasetMetaFile<DatasetMetadata>? = null
      var manifest: DatasetMetaFile<DatasetManifest>? = null
      var uploadFile: DatasetDataFile? = null
      var importableFile: DatasetDataFile? = null
      var installableFile: DatasetDataFile? = null
      var deleteFlag: DatasetFlagFile? = null
      var revisedFlag: DatasetFlagFile? = null

      val shareRefs = HashMap<UserID, ShareRef>(4)

      objects.forEach {
        val path = "${it.bucket.name}/${it.path}"
          .toDatasetPathOrNull()
          ?: throw MalformedDatasetException("Unrecognized file path in S3: " + it.path)

        when (path) {
          is DataFilePath     -> when (path.type) {
            DataFileType.ImportReady  -> importableFile = DatasetDataFile.create(it)
            DataFileType.InstallReady -> installableFile = DatasetDataFile.create(it)
            DataFileType.RawUpload    -> uploadFile = DatasetDataFile.create(it)
          }
          is DocumentFilePath -> { /*ignore*/ }
          is FlagFilePath     -> when (path.type) {
            FlagFileType.Delete  -> deleteFlag  = DatasetFlagFile.create(it)
            FlagFileType.Revised -> revisedFlag = DatasetFlagFile.create(it)
          }
          is MetaFilePath     -> when (path.type) {
            MetaFileType.Metadata -> metaFile = DatasetMetaFile.createMetadata(it)
            MetaFileType.Manifest -> manifest = DatasetMetaFile.createManifest(it)
          }
          is ShareFilePath    -> when (path.type) {
            ShareFileType.Offer   -> shareRefs.computeIfAbsent(path.recipientID, ::ShareRef).offer = it
            ShareFileType.Receipt -> shareRefs.computeIfAbsent(path.recipientID, ::ShareRef).receipt = it
          }
        }
      }

      return MaterializedDatasetDirectory(
        ownerID         = ownerID,
        datasetID       = datasetID,
        bucket          = bucket,
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
        DatasetShare.create(
          recipientID,
          offer?.let { DatasetShareFile.createOffer(it, recipientID) }
            ?: DatasetShareFile.createOffer(pathFactory.datasetShareOfferFile(recipientID), recipientID, receipt!!.bucket),
          receipt?.let { DatasetShareFile.createReceipt(it, recipientID) }
            ?: DatasetShareFile.createReceipt(pathFactory.datasetShareReceiptFile(recipientID), recipientID, offer!!.bucket)
        )
    }

  }

  // Eagerly loaded dataset directory must exist by definition of being constructed.
  override fun exists(): Boolean = true

  override fun hasMetaFile() =
    metaFile != null

  override fun getMetaFile() =
    metaFile ?: DatasetMetaFile.createMetadata(pathFactory.datasetMetaFile(), bucket)

  override fun putMetaFile(meta: DatasetMetadata) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteMetaFile() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasManifestFile() =
    manifest != null

  override fun getManifestFile() =
    manifest ?: DatasetMetaFile.createManifest(pathFactory.datasetManifestFile(), bucket)

  override fun putManifestFile(manifest: DatasetManifest) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteManifestFile() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasDeleteFlag() =
    deleteFlag != null

  override fun getDeleteFlag() =
    deleteFlag ?: DatasetFlagFile.create(pathFactory.datasetDeleteFlagFile(), bucket)

  override fun putDeleteFlag() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteDeleteFlag() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasRevisedFlag() =
    revisedFlag != null

  override fun getRevisedFlag() =
    revisedFlag ?: DatasetFlagFile.create(pathFactory.datasetRevisedFlagFile(), bucket)

  override fun putRevisedFlag() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteRevisedFlag() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasUploadFile() =
    uploadFile != null

  override fun getUploadFile() =
    uploadFile ?: DatasetDataFile.create(pathFactory.datasetUploadZip(), bucket)

  override fun putUploadFile(fn: () -> InputStream) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteUploadFile() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasImportReadyFile() =
    importableFile != null

  override fun getImportReadyFile() =
    importableFile ?: DatasetDataFile.create(pathFactory.datasetImportReadyZip(), bucket)

  override fun putImportReadyFile(fn: () -> InputStream) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteImportReadyFile() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasInstallReadyFile() =
    installableFile != null

  override fun getInstallReadyFile() =
    installableFile ?: DatasetDataFile.create(pathFactory.datasetInstallReadyZip(), bucket)

  override fun putInstallReadyFile(fn: () -> InputStream) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteInstallReadyFile() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun getShares(): Map<UserID, DatasetShare> = shares

  override fun putShare(recipientID: UserID) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun putShare(recipientID: UserID, offer: DatasetShareOffer, receipt: DatasetShareReceipt) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun toString() = "EagerDatasetDir($ownerID/$datasetID)"
}
