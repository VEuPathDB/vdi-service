package vdi.model.data

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetManifest(
  @field:JsonProperty(UserUpload)
  @field:JsonAlias(Legacy_InputFiles)
  val userUploadFiles: List<DatasetFileInfo>,

  @field:JsonProperty(InstallReady)
  @field:JsonAlias(Legacy_DataFiles)
  val installReadyFiles: List<DatasetFileInfo> = emptyList()
) {
  companion object JsonKey {
    const val UserUpload   = "userUpload"
    const val InstallReady = "installReady"

    const val Legacy_InputFiles = "inputFiles"
    const val Legacy_DataFiles  = "dataFiles"
  }
}
