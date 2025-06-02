package vdi.lib.s3.paths

import vdi.model.data.DatasetID
import vdi.model.data.UserID

internal class S3DatasetPathFactory(
  private val ownerID: UserID,
  private val datasetID: DatasetID,
) {
  fun userDir() = S3Paths.userDir(ownerID)

  fun datasetDir() = S3Paths.datasetDir(ownerID, datasetID)

  fun datasetManifestFile() = S3Paths.datasetManifestFile(ownerID, datasetID)

  fun datasetMetaFile() = S3Paths.datasetMetaFile(ownerID, datasetID)

  fun datasetDeleteFlagFile() = S3Paths.datasetDeleteFlagFile(ownerID, datasetID)

  fun datasetRevisedFlagFile() = S3Paths.datasetRevisedFlagFile(ownerID, datasetID)

  fun datasetSharesDir() = S3Paths.datasetSharesDir(ownerID, datasetID)

  fun datasetShareOfferFile(recipientID: UserID) = S3Paths.datasetShareOfferFile(ownerID, datasetID, recipientID)

  fun datasetShareReceiptFile(recipientID: UserID) =
    S3Paths.datasetShareReceiptFile(ownerID, datasetID, recipientID)

  fun datasetImportReadyZip() = S3Paths.datasetImportReadyFile(ownerID, datasetID)

  fun datasetInstallReadyZip() = S3Paths.datasetInstallReadyFile(ownerID, datasetID)

  fun datasetUploadZip() = S3Paths.datasetRawUploadFile(ownerID, datasetID)

  fun datasetDocumentsDir() = S3Paths.datasetDocumentsDir(ownerID, datasetID)

  fun datasetDocumentFile(fileName: String) = S3Paths.datasetDocumentFile(ownerID, datasetID, fileName)
}
