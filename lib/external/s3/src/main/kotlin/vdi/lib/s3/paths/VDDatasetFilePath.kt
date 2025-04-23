package vdi.lib.s3.paths

sealed interface VDDatasetFilePath : vdi.lib.s3.paths.VDPath {
  val fileName: String

  val isMetaFile: Boolean
  val isManifestFile: Boolean
  val isRawUploadFile: Boolean
  val isImportReadyFile: Boolean
  val isInstallReadyFile: Boolean
  val isDeleteFlagFile: Boolean
}

