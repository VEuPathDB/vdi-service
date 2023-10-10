package org.veupathdb.vdi.lib.s3.datasets

import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toUserIDOrNull
import org.veupathdb.vdi.lib.common.model.*
import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.s3.datasets.paths.S3DatasetPathFactory
import vdi.constants.InstallZipName
import java.io.InputStream

internal class DatasetDirectoryImpl(
  override val ownerID: UserID,
  override val datasetID: DatasetID,
  private val bucket: S3Bucket,
  private val pathFactory: S3DatasetPathFactory,
) : DatasetDirectory {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun exists(): Boolean =
    bucket.objects.listSubPaths(pathFactory.datasetDir()).count > 0

  override fun hasMeta() =
    pathFactory.datasetMetaFile() in bucket.objects

  override fun getMeta() =
    DatasetMetaFileImpl(bucket, pathFactory.datasetMetaFile())

  override fun putMeta(meta: VDIDatasetMeta) {
    bucket.objects.put(pathFactory.datasetMetaFile(), JSON.writeValueAsBytes(meta).inputStream())
  }


  override fun hasManifest() =
    pathFactory.datasetManifestFile() in bucket.objects

  override fun getManifest() =
    DatasetManifestFileImpl(bucket, pathFactory.datasetManifestFile())

  override fun putManifest(manifest: VDIDatasetManifest) {
    bucket.objects.put(pathFactory.datasetManifestFile(), JSON.writeValueAsBytes(manifest).inputStream())
  }


  override fun hasDeleteFlag() =
    pathFactory.datasetDeleteFlagFile() in bucket.objects

  override fun getDeleteFlag() =
    DatasetDeleteFlagFileImpl(bucket, pathFactory.datasetDeleteFlagFile())

  override fun putDeleteFlag() {
    bucket.objects.touch(pathFactory.datasetDeleteFlagFile())
  }


  override fun getUploadFiles() =
    bucket.objects.list(pathFactory.datasetUploadsDir())
      .map { DatasetUploadFileImpl(bucket, it.path) }

  override fun putUploadFile(name: String, fn: () -> InputStream) {
    fn().use { bucket.objects.put(pathFactory.datasetUploadFile(name), it) }
  }


  override fun getDataFiles() =
    bucket.objects.list(pathFactory.datasetDataDir())
      .map { DatasetDataFileImpl(bucket, it.path) }

  override fun putDataFile(name: String, fn: () -> InputStream) {
    fn().use { bucket.objects.put(pathFactory.datasetDataFile(name), it) }
  }

  override fun getShares(): Map<UserID, DatasetShare> {
    log.debug("looking up shares for dataset {} owned by user {}", datasetID, ownerID)

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
    log.debug("putting a new share for user {} into dataset {} owned by user {}", recipientID, datasetID, ownerID)

    val offer   = VDIDatasetShareOffer(VDIShareOfferAction.Grant)
    val receipt = VDIDatasetShareReceipt(VDIShareReceiptAction.Accept)

    bucket.objects.put(pathFactory.datasetShareOfferFile(recipientID), JSON.writeValueAsBytes(offer).inputStream())
    bucket.objects.put(pathFactory.datasetShareReceiptFile(recipientID), JSON.writeValueAsBytes(receipt).inputStream())
  }


  override fun isUploadComplete(): Boolean {
    log.debug("testing whether dataset {} (user {}) upload is complete", datasetID, ownerID)

    // If there is no meta file then there is no upload.
    if (!hasMeta())
      return false

    val uploadFiles = getUploadFiles()

    // If there are no uploaded files, then the upload is still in progress.
    if (uploadFiles.isEmpty())
      return false

    if (uploadFiles.size != 1)
      log.warn("dataset {} owned by user {} has more than one upload file present", datasetID, ownerID)

    // If we made it here, then the meta file exists, and there is at least one
    // upload file
    return true
  }

  override fun isImportComplete(): Boolean {
    log.debug("testing whether dataset {} (user {}) upload is complete", datasetID, ownerID)

    // If there is no meta file, then the dataset hasn't even been uploaded yet
    if (!hasMeta())
      return false

    // If there is no manifest file, then the dataset hasn't yet been imported,
    // or is currently in the process of being imported.
    if (!hasManifest())
      return false

    val dataFiles = getDataFiles()

    // If there are no data files, then the import is in progress.
    if (dataFiles.isEmpty())
      return false

    // If the install data zip file does not exist
    return dataFiles.asSequence().filter { it.name == InstallZipName }.any()
  }
}