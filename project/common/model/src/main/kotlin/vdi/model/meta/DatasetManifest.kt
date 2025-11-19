package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetManifest(
  @param:JsonProperty(RawUploadFiles)
  @field:JsonProperty(RawUploadFiles)
  val userUploadFiles: Collection<DatasetFileInfo>,

  @param:JsonProperty(InstallableFiles)
  @field:JsonProperty(InstallableFiles)
  val installReadyFiles: Collection<DatasetFileInfo> = emptyList(),

  @param:JsonProperty(ExtraDocuments)
  @field:JsonProperty(ExtraDocuments)
  val documentFiles: Collection<DatasetFileInfo> = emptyList(),

  @param:JsonProperty(DataDictionaries)
  @field:JsonProperty(DataDictionaries)
  val dataDictionaryFiles: Collection<DatasetFileInfo> = emptyList(),
): VersionedMetaObject {
  companion object JsonKey {
    const val RawUploadFiles   = "raw-upload"
    const val InstallableFiles = "install-ready"
    const val ExtraDocuments   = "document-files"
    const val DataDictionaries = "data-dictionaries"
  }
}