package vdi.components.datasets.paths

internal class S3DatasetPathFactory(
  private val root: String,
  private val ownerID: String,
  private val datasetID: String,
) {
  fun rootDir() = S3Paths.rootDir(root)

  fun userDir() = S3Paths.userDir(root, ownerID)

  fun datasetDir() = S3Paths.datasetDir(root, ownerID, datasetID)


  fun datasetManifestFile() = S3Paths.datasetManifestFile(root, ownerID, datasetID)

  fun datasetMetaFile() = S3Paths.datasetMetaFile(root, ownerID, datasetID)

  fun datasetDeleteFlagFile() = S3Paths.datasetDeleteFlagFile(root, ownerID, datasetID)


  fun datasetDataDir() = S3Paths.datasetDataDir(root, ownerID, datasetID)

  fun datasetDataFile(fileName: String) = S3Paths.datasetDataFile(root, ownerID, datasetID, fileName)


  fun datasetSharesDir() = S3Paths.datasetSharesDir(root, ownerID, datasetID)

  fun datasetShareOfferFile(recipientID: String) = S3Paths.datasetShareOfferFile(root, ownerID, datasetID, recipientID)

  fun datasetShareReceiptFile(recipientID: String) =
    S3Paths.datasetShareReceiptFile(root, ownerID, datasetID, recipientID)


  fun datasetUploadsDir() = S3Paths.datasetUploadsDir(root, ownerID, datasetID)

  fun datasetUploadFile(fileName: String) = S3Paths.datasetUploadFile(root, ownerID, datasetID, fileName)
}