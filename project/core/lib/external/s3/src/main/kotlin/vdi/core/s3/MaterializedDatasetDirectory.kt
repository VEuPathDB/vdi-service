package vdi.core.s3

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import vdi.model.meta.DatasetManifest
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.DatasetShareOffer
import vdi.model.meta.DatasetShareReceipt
import java.io.InputStream
import vdi.core.s3.exception.MalformedDatasetException
import vdi.core.s3.files.FileName
import vdi.core.s3.files.data.DataFile
import vdi.core.s3.files.data.ImportReadyFile
import vdi.core.s3.files.data.InstallReadyFile
import vdi.core.s3.files.data.RawUploadFile
import vdi.core.s3.files.docs.DocumentFile
import vdi.core.s3.files.flags.DeleteFlagFile
import vdi.core.s3.files.flags.FlagFile
import vdi.core.s3.files.flags.RevisedFlagFile
import vdi.core.s3.files.maps.MappingFile
import vdi.core.s3.files.meta.ManifestFile
import vdi.core.s3.files.meta.MetaFile
import vdi.core.s3.files.meta.MetadataFile
import vdi.core.s3.files.shares.ShareOffer
import vdi.core.s3.files.shares.ShareReceipt
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

  private val metaFile: MetadataFile?,
  private val manifest: ManifestFile?,
  private val uploadFile: DataFile?,
  private val importableFile: DataFile?,
  private val installableFile: DataFile?,
  private val deleteFlag: FlagFile?,
  private val revisedFlag: FlagFile?,
  private val shares: Map<UserID, Pair<ShareOffer, ShareReceipt>>,
  private val mappingFiles: List<MappingFile>,
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
      var metaFile: MetadataFile? = null
      var manifest: ManifestFile? = null
      var uploadFile: DataFile? = null
      var importableFile: DataFile? = null
      var installableFile: DataFile? = null
      var deleteFlag: FlagFile? = null
      var revisedFlag: FlagFile? = null

      val shareRefBuilders = HashMap<UserID, ShareRefBuilder>(4)
      val mappingFiles = ArrayList<MappingFile>(1)

      objects.forEach {
        val path = "${it.bucket.name}/${it.path}"
          .toDatasetPathOrNull()
          ?: throw MalformedDatasetException("Unrecognized file path in S3: " + it.path)

        when (path) {
          is DataFilePath -> when (path.fileName) {
            FileName.ImportReadyFile  -> importableFile = ImportReadyFile(it)
            FileName.InstallReadyFile -> installableFile = InstallReadyFile(it)
            FileName.RawUploadFile    -> uploadFile = RawUploadFile(it)
          }

          is DocumentFilePath -> { /*ignore*/ }

          is FlagFilePath -> when (path.fileName) {
            FileName.DeleteFlagFile  -> deleteFlag  = DeleteFlagFile(it)
            FileName.RevisedFlagFile -> revisedFlag = RevisedFlagFile(it)
          }

          is MetaFilePath -> when (path.fileName) {
            FileName.MetadataFile -> metaFile = MetaFile(it)
            FileName.ManifestFile -> manifest = ManifestFile(it)
          }

          is ShareFilePath -> when (path.fileName) {
            FileName.ShareOfferFile   -> shareRefBuilders.computeIfAbsent(path.recipientID, ::ShareRefBuilder).offer = it
            FileName.ShareReceiptFile -> shareRefBuilders.computeIfAbsent(path.recipientID, ::ShareRefBuilder).receipt = it
          }

          is MappingFilePath -> mappingFiles.add(MappingFile(it))
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
        shares          = shareRefBuilders.mapValues { (_, ref) -> ref.build(pathFactory) },
        pathFactory     = pathFactory,
        mappingFiles    = mappingFiles,
      )
    }

    private data class ShareRefBuilder(
      val recipientID: UserID,
      var offer: S3Object? = null,
      var receipt: S3Object? = null,
    ) {
      fun build(pathFactory: S3DatasetPathFactory) =
        Pair(
          offer?.let { ShareOffer(recipientID, it) }
            ?: ShareOffer(recipientID, pathFactory.datasetShareOfferFile(recipientID), receipt!!.bucket.objects),
          receipt?.let { ShareReceipt(recipientID, it) }
            ?: ShareReceipt(recipientID, pathFactory.datasetShareReceiptFile(recipientID), offer!!.bucket.objects),
        )
    }
  }

  // Eagerly loaded dataset directory must exist by definition of being constructed.
  override fun exists(): Boolean = true

  override fun hasMetaFile() =
    metaFile != null

  override fun getMetaFile() =
    metaFile ?: MetaFile(pathFactory.datasetMetaFile(), bucket.objects)

  override fun putMetaFile(meta: DatasetMetadata) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteMetaFile() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasManifestFile() =
    manifest != null

  override fun getManifestFile() =
    manifest ?: ManifestFile(pathFactory.datasetManifestFile(), bucket.objects)

  override fun putManifestFile(manifest: DatasetManifest) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteManifestFile() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasDeleteFlag() =
    deleteFlag != null

  override fun getDeleteFlag() =
    deleteFlag ?: DeleteFlagFile(pathFactory.datasetDeleteFlagFile(), bucket.objects)

  override fun putDeleteFlag() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteDeleteFlag() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasRevisedFlag() =
    revisedFlag != null

  override fun getRevisedFlag() =
    revisedFlag ?: RevisedFlagFile(pathFactory.datasetRevisedFlagFile(), bucket.objects)

  override fun putRevisedFlag() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteRevisedFlag() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasUploadFile() =
    uploadFile != null

  override fun getUploadFile() =
    uploadFile ?: RawUploadFile(pathFactory.datasetUploadZip(), bucket.objects)

  override fun putUploadFile(fn: () -> InputStream) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteUploadFile() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasImportReadyFile() =
    importableFile != null

  override fun getImportReadyFile() =
    importableFile ?: ImportReadyFile(pathFactory.datasetImportReadyZip(), bucket.objects)

  override fun putImportReadyFile(fn: () -> InputStream) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteImportReadyFile() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun hasInstallReadyFile() =
    installableFile != null

  override fun getInstallReadyFile() =
    installableFile ?: InstallReadyFile(pathFactory.datasetInstallReadyZip(), bucket.objects)

  override fun putInstallReadyFile(fn: () -> InputStream) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteInstallReadyFile() =
    throw UnsupportedOperationException("${javaClass.name} is read-only")


  override fun getShares() = shares

  override fun putShare(recipientID: UserID) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun putShare(recipientID: UserID, offer: DatasetShareOffer, receipt: DatasetShareReceipt) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun getMappingFiles() = mappingFiles.asSequence()

  override fun getMappingFile(name: String) = mappingFiles.firstOrNull { it.baseName == name }

  override fun putMappingFile(name: String, fn: () -> InputStream) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteMappingFile(name: String) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun getDocumentFiles() = emptySequence<DocumentFile>()

  override fun getDocumentFile(name: String) = null

  override fun putDocumentFile(name: String, fn: () -> InputStream) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun deleteDocumentFile(name: String) =
    throw UnsupportedOperationException("${javaClass.name} is read-only")

  override fun toString() = "EagerDatasetDir($ownerID/$datasetID)"
}
