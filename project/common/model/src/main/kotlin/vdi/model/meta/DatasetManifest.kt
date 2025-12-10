package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetManifest(
  @param:JsonAlias(LegacyRawUploadFiles)
  @param:JsonProperty(RawUploadFiles)
  @field:JsonProperty(RawUploadFiles)
  val userUploadFiles: Collection<DatasetFileInfo>,

  @param:JsonAlias(LegacyInstallableFiles)
  @param:JsonProperty(InstallableFiles)
  @field:JsonProperty(InstallableFiles)
  val installReadyFiles: Collection<DatasetFileInfo> = emptyList(),

  @param:JsonProperty(ExtraDocuments)
  @field:JsonProperty(ExtraDocuments)
  val documentFiles: Collection<DatasetFileInfo> = emptyList(),

  @param:JsonProperty(DataDictionaries)
  @field:JsonProperty(DataDictionaries)
  val dataDictionaryFiles: Collection<DatasetFileInfo> = emptyList(),
) {
  companion object JsonKey {
    const val RawUploadFiles   = "rawUploads"
    const val InstallableFiles = "installReady"
    const val ExtraDocuments   = "documentFiles"
    const val DataDictionaries = "dataProperties"

    private const val LegacyRawUploadFiles = "inputFiles"
    private const val LegacyInstallableFiles = "dataFiles"
  }
}