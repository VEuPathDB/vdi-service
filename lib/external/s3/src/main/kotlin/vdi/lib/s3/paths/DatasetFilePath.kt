package vdi.lib.s3.paths

sealed interface DatasetFilePath : DatasetPath {
  val isMetaFile: Boolean
  val isManifestFile: Boolean
  val isRawUploadFile: Boolean
  val isImportReadyFile: Boolean
  val isInstallReadyFile: Boolean
  val isDeleteFlagFile: Boolean
}

