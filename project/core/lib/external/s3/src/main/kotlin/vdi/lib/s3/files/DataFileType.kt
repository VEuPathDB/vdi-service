package vdi.lib.s3.files

enum class DataFileType(val fileName: String) {
  ImportReady(FileName.ImportReadyFile),
  InstallReady(FileName.InstallReadyFile),
  RawUpload(FileName.RawUploadFile);
}