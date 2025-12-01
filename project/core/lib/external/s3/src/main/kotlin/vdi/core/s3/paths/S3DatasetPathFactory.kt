package vdi.core.s3.paths

import vdi.model.meta.DatasetID
import vdi.model.meta.UserID

internal class S3DatasetPathFactory(
  private val ownerID: UserID,
  private val datasetID: DatasetID,
) {
  fun datasetDir() = S3Paths.datasetDir(ownerID, datasetID)

  fun manifestFile() = S3Paths.datasetManifestFile(ownerID, datasetID)

  fun metadataFile() = S3Paths.datasetMetaFile(ownerID, datasetID)

  fun deleteFlagFile() = S3Paths.datasetDeleteFlagFile(ownerID, datasetID)

  fun revisedFlagFile() = S3Paths.datasetRevisedFlagFile(ownerID, datasetID)

  fun sharesDir() = S3Paths.datasetSharesDir(ownerID, datasetID)

  fun shareOfferFile(recipientID: UserID) = S3Paths.datasetShareOfferFile(ownerID, datasetID, recipientID)

  fun shareReceiptFile(recipientID: UserID) =
    S3Paths.datasetShareReceiptFile(ownerID, datasetID, recipientID)

  fun importReadyZip() = S3Paths.datasetImportReadyFile(ownerID, datasetID)

  fun installReadyZip() = S3Paths.datasetInstallReadyFile(ownerID, datasetID)

  fun uploadZip() = S3Paths.datasetRawUploadFile(ownerID, datasetID)

  fun documentsDir() = S3Paths.datasetDocumentsDir(ownerID, datasetID)

  fun documentFile(fileName: String) = S3Paths.datasetDocumentFile(ownerID, datasetID, fileName)

  fun mappingsDir() = S3Paths.variablePropertiesDir(ownerID, datasetID)

  fun mappingFile(fileName: String) = S3Paths.variablePropertiesFile(ownerID, datasetID, fileName)
}
