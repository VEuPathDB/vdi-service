package org.veupathdb.vdi.lib.s3.datasets

import org.veupathdb.lib.s3.s34k.objects.S3Object
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toUserIDOrNull
import org.veupathdb.vdi.lib.common.model.*
import org.veupathdb.vdi.lib.s3.datasets.exception.MalformedDatasetException
import org.veupathdb.vdi.lib.s3.datasets.paths.S3DatasetPathFactory
import org.veupathdb.vdi.lib.s3.datasets.paths.S3Paths
import vdi.constants.InstallZipName
import java.io.InputStream
import java.util.function.Function
import java.util.stream.Collectors

/**
 * Implementation of a DatasetDirectory in which all objects are already known and metadata is in memory at the time
 * of construction. This allows perusing a known dataset's files without making remote calls to the underlying data store.
 */
internal class EagerlyLoadedDatasetDirectory(
  override val ownerID: UserID,
  override val datasetID: DatasetID,
  private val pathFactory: S3DatasetPathFactory,
  datasetFiles: List<DatasetFile>
  ) : DatasetDirectory {

  private val metaFile: DatasetMetaFile?
  private val deleteFlag: DatasetDeleteFlagFile?
  private val manifest: DatasetManifestFile?
  private val datasetDataFiles: List<DatasetDataFile>
  private val shareReceiptFiles: List<DatasetShareReceiptFile>
  private val shareOfferFiles: List<DatasetShareOfferFile>
  private val uploadFiles: List<DatasetUploadFile>

  init {
    metaFile = datasetFiles.find { it is DatasetMetaFile } as DatasetMetaFile?
    deleteFlag = datasetFiles.find { it is DatasetDeleteFlagFile } as DatasetDeleteFlagFile?
    manifest = datasetFiles.find { it is DatasetManifestFile } as DatasetManifestFile?
    datasetDataFiles = datasetFiles.filterIsInstance<DatasetDataFile>()
    shareReceiptFiles = datasetFiles.filterIsInstance<DatasetShareReceiptFile>()
    shareOfferFiles = datasetFiles.filterIsInstance<DatasetShareOfferFile>()
    uploadFiles = datasetFiles.filterIsInstance<DatasetUploadFile>()
  }

  /**
   * Construct a dataset from a list of S3Objects.
   *
   * @param objects A list of S3 objects sorted by UD ID. This will be sorted naturally when listing objects from S3
   * since listed objects are sorted in utf-8 byte order of key.
   * @param ownerID The owner of the user dataset.
   * @param datasetID Identifier of the user dataset.
   * @param pathFactory S3PathFactory for looking up s3 paths.
   * @throws IllegalArgumentException if any of the S3Objects cannot be identified as a dataset file.
   */
  constructor(
    objects: List<S3Object>,
    ownerID: UserID,
    datasetID: DatasetID,
    pathFactory: S3DatasetPathFactory
  ) : this(ownerID, datasetID, pathFactory, objects.map { it.toDatasetFile(pathFactory) })

  override fun exists(): Boolean = true // Eagerly loaded dataset directory must exist by definition of being constructed.

  override fun hasMeta() = metaFile != null

  override fun getMeta() = metaFile!!

  override fun putMeta(meta: VDIDatasetMeta) {
    throw UnsupportedOperationException("DatasetDirectoryEagerImpl is read-only")
  }


  override fun hasManifest() = manifest != null

  override fun getManifest() = manifest!!

  override fun putManifest(manifest: VDIDatasetManifest) {
    throw UnsupportedOperationException("DatasetDirectoryEagerImpl is read-only")
  }


  override fun hasDeleteFlag() = deleteFlag != null

  override fun getDeleteFlag() = deleteFlag!!

  override fun putDeleteFlag() {
    throw UnsupportedOperationException("DatasetDirectoryEagerImpl is read-only")
  }


  override fun getUploadFiles() = uploadFiles

  override fun putUploadFile(name: String, fn: () -> InputStream) {
    throw UnsupportedOperationException("DatasetDirectoryEagerImpl is read-only")
  }


  override fun getDataFiles() = datasetDataFiles

  override fun putDataFile(name: String, fn: () -> InputStream) {
    throw UnsupportedOperationException("DatasetDirectoryEagerImpl is read-only")
  }

  override fun getShares(): Map<UserID, DatasetShare> {
    val pathPrefix = pathFactory.datasetSharesDir()
    return shareOfferFiles.stream()
      .filter { offerFile ->
        val recipientID = offerFile.path.substring(pathPrefix.length).split("/")[0].toUserIDOrNull()
          ?: throw IllegalStateException("invalid user ID")
        shareReceiptFiles.find { receiptFile ->
          recipientID == receiptFile.path.substring(pathPrefix.length).split("/")[0].toUserIDOrNull()
        } != null
      }
      .map { offerFile ->
        val recipientID = offerFile.path.substring(pathPrefix.length).split("/")[0].toUserIDOrNull()
          ?: throw IllegalStateException("invalid user ID")
        val receiptFile = shareReceiptFiles.find { receiptFile ->
          recipientID == receiptFile.path.substring(pathPrefix.length).split("/")[0].toUserIDOrNull()
        }!!
        DatasetShareImpl(recipientID, offerFile, receiptFile)
      }.collect(Collectors.toMap(DatasetShareImpl::recipientID, Function.identity()))
  }

  override fun putShare(recipientID: UserID) {
    throw UnsupportedOperationException("DatasetDirectoryEagerImpl is read-only")
  }

  override fun isImportComplete(): Boolean {
    if (!hasMeta())
      return false

    if (!hasManifest())
      return false

    if (datasetDataFiles.isEmpty())
      return false

    manifest!!.load()!!

    return datasetDataFiles.asSequence().filter { it.name == InstallZipName }.any()
  }

  override fun isUploadComplete(): Boolean {
    if (!hasMeta())
      return false
    if (uploadFiles.isEmpty())
      return false
    return true
  }
}

private fun S3Object.toDatasetFile(pathFactory: S3DatasetPathFactory): DatasetFile {
  val datasetFile = when {
    this.path.contains(pathFactory.datasetDataDir()) -> DatasetDataFileImpl(this)
    this.path.contains(pathFactory.datasetMetaFile()) -> DatasetMetaFileImpl(this)
    this.path.contains(pathFactory.datasetManifestFile()) -> DatasetManifestFileImpl(this)
    this.path.contains(pathFactory.datasetSharesDir()) && this.baseName == S3Paths.SHARE_OFFER_FILE_NAME -> DatasetShareOfferFileImpl(
      this
    )
    this.path.contains(pathFactory.datasetSharesDir()) && this.baseName == S3Paths.SHARE_RECEIPT_FILE_NAME -> DatasetShareReceiptFileImpl(
      this
    )
    this.path.contains(pathFactory.datasetDeleteFlagFile()) -> DatasetDeleteFlagFileImpl(this)
    this.path.contains(pathFactory.datasetUploadsDir()) -> DatasetUploadFileImpl(this)
    else -> throw MalformedDatasetException("Unrecognized file path in S3: " + this.path)
  }
  return datasetFile
}
