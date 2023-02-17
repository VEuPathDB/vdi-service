package vdi.components.datasets

import vdi.components.common.DatasetID

private val sharePattern = Regex("^(\\d+)/([0-9a-fA-F]{32})/imported/share/(\\d+)/.+$")

internal object S3Path {

  fun datasetDirectory(ownerID: Long, datasetID: DatasetID) =
    "$ownerID/$datasetID"


  fun uploadDir(ownerID: Long, datasetID: DatasetID) =
    "$ownerID/$datasetID/uploaded/"

  fun uploadFile(ownerID: Long, datasetID: DatasetID, fileName: String) =
    "$ownerID/$datasetID/uploaded/$fileName"


  fun dataDir(ownerID: Long, datasetID: DatasetID) =
    "$ownerID/$datasetID/imported/data/"

  fun dataFile(ownerID: Long, datasetID: DatasetID, fileName: String) =
    "$ownerID/$datasetID/imported/data/$fileName"


  fun shareDir(ownerID: Long, datasetID: DatasetID) =
    "$ownerID/$datasetID/imported/share/"

  fun shareOwnerStateFile(ownerID: Long, datasetID: DatasetID, recipientID: Long) =
    "$ownerID/$datasetID/imported/share/$recipientID/owner-state.json"

  fun shareRecipientStateFile(ownerID: Long, datasetID: DatasetID, recipientID: Long) =
    "$ownerID/$datasetID/imported/share/$recipientID/recipient-state.json"


  fun manifestFile(ownerID: Long, datasetID: DatasetID) =
    "$ownerID/$datasetID/imported/manifest.json"

  fun metaFile(ownerID: Long, datasetID: DatasetID) =
    "$ownerID/$datasetID/imported/meta.json"

  fun deletedFlagFile(ownerID: Long, datasetID: DatasetID) =
    "$ownerID/$datasetID/imported/deleted-flag"
}