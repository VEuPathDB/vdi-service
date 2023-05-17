package org.veupathdb.vdi.lib.s3.datasets.paths

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

internal class S3DatasetPathFactory(
  private val ownerID: UserID,
  private val datasetID: DatasetID,
) {
  fun rootDir() = S3Paths.rootDir()

  fun userDir() = S3Paths.userDir(ownerID)

  fun datasetDir() = S3Paths.datasetDir(ownerID, datasetID)


  fun datasetManifestFile() = S3Paths.datasetManifestFile(ownerID, datasetID)

  fun datasetMetaFile() = S3Paths.datasetMetaFile(ownerID, datasetID)

  fun datasetDeleteFlagFile() = S3Paths.datasetDeleteFlagFile(ownerID, datasetID)


  fun datasetDataDir() = S3Paths.datasetDataDir(ownerID, datasetID)

  fun datasetDataFile(fileName: String) = S3Paths.datasetDataFile(ownerID, datasetID, fileName)


  fun datasetSharesDir() = S3Paths.datasetSharesDir(ownerID, datasetID)

  fun datasetShareOfferFile(recipientID: UserID) = S3Paths.datasetShareOfferFile(ownerID, datasetID, recipientID)

  fun datasetShareReceiptFile(recipientID: UserID) =
    S3Paths.datasetShareReceiptFile(ownerID, datasetID, recipientID)


  fun datasetUploadsDir() = S3Paths.datasetUploadsDir(ownerID, datasetID)

  fun datasetUploadFile(fileName: String) = S3Paths.datasetUploadFile(ownerID, datasetID, fileName)
}